package com.sethchhim.kuboo_remote.client

import android.content.Context
import com.sethchhim.kuboo_remote.util.Settings.ALLOW_SELF_SIGNED_CERTIFICATES
import com.sethchhim.kuboo_remote.util.Settings.CACHE_SIZE
import com.sethchhim.kuboo_remote.util.Settings.CONNECTION_TIMEOUT
import com.sethchhim.kuboo_remote.util.Settings.READ_TIMEOUT
import okhttp3.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

class OkHttpClient(private val context: Context) : OkHttpClient() {

    internal val sslHandshakeInterceptor = SSLHandshakeInterceptor()

    private val isHttpLoggingEnabled = false

    override fun cache(): Cache {
        val cacheFile = File("${context.cacheDir}okhttp_cache")
        if (!cacheFile.exists()) cacheFile.mkdir()
        return Cache(cacheFile, (CACHE_SIZE * 1024 * 1024).toLong())
    }

    override fun cookieJar() = cookieJar

    override fun connectTimeoutMillis() = CONNECTION_TIMEOUT

    /**
     * The name on the certificate has to be the server being talked to, unless the user has
     * accepted certificates that do not validate.
     *
     * This used to return true for every host, which together with the trust manager below meant
     * an https connection verified nothing at all: any machine on the path could answer as the
     * server and be handed the password.
     */
    override fun hostnameVerifier(): HostnameVerifier = when (ALLOW_SELF_SIGNED_CERTIFICATES) {
        true -> HostnameVerifier { _, _ -> true }
        false -> defaultHostnameVerifier
    }

    override fun interceptors(): MutableList<Interceptor> {
        val interceptorList = mutableListOf<Interceptor>()
        interceptorList.add(sslHandshakeInterceptor)
        interceptorList.add(UbooquitySessionInterceptor(cookieJar) { this })

        if (isHttpLoggingEnabled) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            interceptorList.add(httpLoggingInterceptor)
        }

        return interceptorList
    }

    override fun protocols() = mutableListOf(Protocol.HTTP_1_1)

    override fun readTimeoutMillis() = READ_TIMEOUT

    /**
     * The platform's own certificate validation, unless the user has chosen to accept
     * certificates that do not validate -- a Ubooquity at home usually presents one it signed
     * itself, and that choice is theirs to make knowingly.
     */
    override fun sslSocketFactory(): SSLSocketFactory {
        if (!ALLOW_SELF_SIGNED_CERTIFICATES) return defaultSslSocketFactory

        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagerArray, java.security.SecureRandom())
            return sslContext.socketFactory
        } catch (e: KeyManagementException) {
            Timber.e("Failed to build an ssl context: ${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            Timber.e("Failed to build an ssl context: ${e.message}")
        }
        return defaultSslSocketFactory
    }

    private val defaultSslSocketFactory: SSLSocketFactory by lazy { SSLContext.getDefault().socketFactory }

    private val defaultHostnameVerifier: HostnameVerifier by lazy { HttpsURLConnection.getDefaultHostnameVerifier() }

    /**
     * Cookies are kept per host, which is what a server session needs.
     *
     * Keying them by the full url instead meant a session was only ever sent back to the exact
     * address that set it: Ubooquity hands out its session when the opds feed is fetched, and
     * that session is what /pagereader/ checks -- it ignores the basic authentication header
     * entirely and answers an "Authentication error" image without one. Page and cover requests
     * therefore came back as that error image on any server with user management enabled.
     */
    private val cookieJar: CookieJar
        get() = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                cookieStore[url.host()] = cookies
            }

            override fun loadForRequest(url: HttpUrl) = cookieStore[url.host()] ?: mutableListOf()
        }

    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    private val trustManagerArray: Array<TrustManager>
        get() = arrayOf(x509TrustManager)

    private val x509TrustManager: X509TrustManager
        get() = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }

}





