This lets you troubleshoot how your Java environment is connecting to HTTPS sites.

It tries to connect with and without a [X509TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/X509TrustManager.html)/[TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html).

For example, with the following arguments:

    java -jar test-ssl-connection.jar https://letsencrypt.org

**NB** Use the `-Djavax.net.ssl.keyStore=<PATH>` to specify a different trust store.
**NB** For more information see the [Java Secure Socket Extension (JSSE) Reference Guide](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html). 

It will dump something like:

```
2016-06-27 21:24:27.135 JAVA_HOME /usr/lib/jvm/java-8-openjdk/jre
2016-06-27 21:24:27.137 key store javax.net.ssl.keyStore=/usr/lib/jvm/java-8-openjdk/jre/lib/security/cacerts (exists)
2016-06-27 21:24:27.270 system property awt.toolkit=sun.awt.X11.XToolkit
2016-06-27 21:24:27.271 system property file.encoding=UTF-8
...
2016-06-27 21:24:27.376 #
2016-06-27 21:24:27.376 # checking WITHOUT trust manager
2016-06-27 21:24:27.376 #
2016-06-27 21:24:27.377 connecting to https://letsencrypt.org...
2016-06-27 21:24:27.672 connected
2016-06-27 21:24:27.672 getting input stream...
2016-06-27 21:24:28.001 checkServerTrusted authType=ECDHE_RSA
2016-06-27 21:24:28.001 checkServerTrusted certificate #0 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.001 checkServerTrusted certificate #0 subject=C=US, ST=California, L=Mountain View, O=INTERNET SECURITY RESEARCH GROUP, CN=letsencrypt.org issuer=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 250048...
  public exponent: 65537
2016-06-27 21:24:28.007 checkServerTrusted certificate #1 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.009 checkServerTrusted certificate #1 subject=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US issuer=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 191141...
  public exponent: 65537
2016-06-27 21:24:28.022 checkServerTrusted certificate #2 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.023 checkServerTrusted certificate #2 subject=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US issuer=CN=DST Root CA X3, O=Digital Signature Trust Co. publicKey=Sun RSA public key, 4096 bits
  modulus: 682577...
  public exponent: 65537
2016-06-27 21:24:28.140 got input stream
2016-06-27 21:24:28.140 response 200 OK
2016-06-27 21:24:28.140 Cipher Suite TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2016-06-27 21:24:28.141 certificate #0 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.141 certificate #0 subject=C=US, ST=California, L=Mountain View, O=INTERNET SECURITY RESEARCH GROUP, CN=letsencrypt.org issuer=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 250048...
  public exponent: 65537
2016-06-27 21:24:28.142 certificate #1 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.142 certificate #1 subject=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US issuer=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 191141...
  public exponent: 65537
2016-06-27 21:24:28.144 certificate #2 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.144 certificate #2 subject=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US issuer=CN=DST Root CA X3, O=Digital Signature Trust Co. publicKey=Sun RSA public key, 4096 bits
  modulus: 682577...
  public exponent: 65537
2016-06-27 21:24:28.147 #
2016-06-27 21:24:28.147 # checking WITH default trust manager
2016-06-27 21:24:28.147 #
2016-06-27 21:24:28.147 connecting to https://letsencrypt.org...
2016-06-27 21:24:28.147 connected
2016-06-27 21:24:28.147 getting input stream...
2016-06-27 21:24:28.499 got input stream
2016-06-27 21:24:28.499 response 200 OK
2016-06-27 21:24:28.499 Cipher Suite TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2016-06-27 21:24:28.500 certificate #0 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.500 certificate #0 subject=C=US, ST=California, L=Mountain View, O=INTERNET SECURITY RESEARCH GROUP, CN=letsencrypt.org issuer=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 250048...
  public exponent: 65537
2016-06-27 21:24:28.501 certificate #1 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.501 certificate #1 subject=CN=TrustID Server CA A52, OU=TrustID Server, O=IdenTrust, C=US issuer=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US publicKey=Sun RSA public key, 2048 bits
  modulus: 191141...
  public exponent: 65537
2016-06-27 21:24:28.502 certificate #2 type=X.509 class=class sun.security.x509.X509CertImpl
2016-06-27 21:24:28.503 certificate #2 subject=CN=IdenTrust Commercial Root CA 1, O=IdenTrust, C=US issuer=CN=DST Root CA X3, O=Digital Signature Trust Co. publicKey=Sun RSA public key, 4096 bits
  modulus: 682577...
  public exponent: 65537
...
```