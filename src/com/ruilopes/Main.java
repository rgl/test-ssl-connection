package com.ruilopes;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final Formatter formatter = new Formatter(System.out, Locale.US);

        //
        // dump JAVA_HOME.
        formatter.format("%s JAVA_HOME %s\n",
                getFormattedDate(),
                System.getProperty("java.home"));

        //
        // try to deduce the trust store path.
        File keyStoreFile = new File(System.getProperty("javax.net.ssl.keyStore", ""));
        if (!keyStoreFile.exists()) {
            keyStoreFile = new File(System.getProperty("java.home"), "lib/security/jssecacerts");
        }
        if (!keyStoreFile.exists()) {
            keyStoreFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
        }
        formatter.format("%s key store javax.net.ssl.keyStore=%s (%s)\n",
                getFormattedDate(),
                keyStoreFile.getAbsolutePath(),
                keyStoreFile.exists() ? "exists" : "DOES NOT EXIST");

        //
        // dump the system properties.
        for (Map.Entry<Object, Object> es : System.getProperties()
                .entrySet()
                .stream()
                .sorted((a, b) -> a.getKey().toString().compareToIgnoreCase(b.getKey().toString()))
                .collect(Collectors.toList())) {
            formatter.format("%s system property %s=%s\n",
                    getFormattedDate(),
                    es.getKey(),
                    es.getValue());
        }

        //
        // connect to the given sites and dump the results.
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(
                    null,
                    new TrustManager[] {
                            new X509TrustManager() {
                                @Override
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                @Override
                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                    formatter.format("%s checkServerTrusted authType=%s\n", getFormattedDate(), authType);
                                    dumpCertificates(formatter, " checkServerTrusted", chain);
                                }

                                @Override
                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }
                            }
                    },
                    null
            );

            boolean errors = false;

            for (String url : args) {
                formatter.format("%s #\n", getFormattedDate());
                formatter.format("%s # checking WITHOUT trust manager\n", getFormattedDate());
                formatter.format("%s #\n", getFormattedDate());
                check(formatter, context.getSocketFactory(), url);

                formatter.format("%s #\n", getFormattedDate());
                formatter.format("%s # checking WITH default trust manager\n", getFormattedDate());
                formatter.format("%s #\n", getFormattedDate());
                errors |= !check(formatter, null, url);
            }

            System.exit(errors ? 1 : 0);
        }
        catch (Exception e) {
            formatter.format(
                    "%s exception class=%s message=%s\n",
                    getFormattedDate(),
                    e.getClass(),
                    e
            );
            System.exit(1);
        }
    }

    private final static DateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static String getFormattedDate() {
        return logDateFormat.format(new Date());
    }

    private static boolean check(final Formatter formatter, SSLSocketFactory socketFactory, String url) {
        for (int retryNumber = 0; retryNumber < 3; ++retryNumber) {
            if (retryNumber > 0) {
                formatter.format("%s ## retry #%d...\n", getFormattedDate(), retryNumber);
            }

            if (checkInternal(formatter, socketFactory, url)) {
                return true;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                formatter.format(
                        "%s exception class=%s message=%s\n",
                        getFormattedDate(),
                        e.getClass(),
                        e
                );
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }

    private static boolean checkInternal(final Formatter formatter, SSLSocketFactory socketFactory, String checkUrl) {
        try {
            URL url = new URL(checkUrl);

            formatter.format("%s connecting to %s...\n", getFormattedDate(), url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            formatter.format("%s connected\n", getFormattedDate());

            try {
                if (socketFactory != null) {
                    connection.setSSLSocketFactory(socketFactory);
                }

                connection.setInstanceFollowRedirects(false);

                formatter.format("%s getting input stream...\n", getFormattedDate());

                InputStream in = new BufferedInputStream(connection.getInputStream());
                try {
                    formatter.format("%s got input stream\n", getFormattedDate());

                    formatter.format("%s response %d %s\n", getFormattedDate(), connection.getResponseCode(), connection.getResponseMessage());

                    // Handle Network Sign-On: Some Wi-Fi networks block Internet access until the
                    // user clicks through a sign-on page. Such sign-on pages are typically
                    // presented by using HTTP redirects.
                    URL actualUrl = connection.getURL();
                    if (!url.getHost().equals(actualUrl.getHost())) {
                        formatter.format("%s got redirected to %s...\n", getFormattedDate(), actualUrl);
                    }

                    formatter.format("%s Cipher Suite %s\n", getFormattedDate(), connection.getCipherSuite());

                    dumpCertificates(formatter, "", connection.getServerCertificates());
                }
                finally {
                    in.close();
                }

                return true;
            }
            finally {
                connection.disconnect();
            }
        }
        catch (Exception e) {
            // Some ISP and DNS providers intercept failed (or the first) DNS queries and show some
            // kind of search results page. This might be the cause of spurious errors like:
            //
            //  javax.net.ssl.SSLHandshakeException: javax.net.ssl.SSLProtocolException: SSL
            //  handshake aborted: ssl=0x56b376a0: Failure in SSL library, usually a protocol error
            //  error:140770FC:SSL routines:SSL23_GET_SERVER_HELLO:unknown protocol
            //  (external/openssl/ssl/s23_clnt.c:766 0x52d32dc5:0x00000000)

            formatter.format(
                    "%s exception class=%s message=%s\n",
                    getFormattedDate(),
                    e.getClass(),
                    e
            );

            return false;
        }
    }

    private static void dumpCertificates(Formatter formatter, String prefix, Certificate[] certificates) {
        if (certificates == null || certificates.length == 0) {
            formatter.format("%s%s peer didn't sent any certificate!?\n", getFormattedDate(), prefix);
        }
        else {
            for (int i = 0; i < certificates.length; ++i) {
                Certificate certificate = certificates[i];

                formatter.format("%s%s certificate #%d type=%s class=%s\n", getFormattedDate(), prefix, i, certificate.getType(), certificate.getClass());

                if (certificate instanceof X509Certificate) {
                    X509Certificate x509 = (X509Certificate) certificate;

                    formatter.format(
                            "%s%s certificate #%d subject=%s issuer=%s publicKey=%s\n",
                            getFormattedDate(),
                            prefix,
                            i,
                            x509.getSubjectX500Principal(),
                            x509.getIssuerX500Principal(),
                            x509.getPublicKey()
                    );
                }
            }
        }
    }
}
