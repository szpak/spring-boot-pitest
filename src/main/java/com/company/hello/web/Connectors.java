package com.company.hello.web;

import com.company.hello.util.Arguments;
import com.google.common.base.Strings;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public final class Connectors {

  static final String SCHEME_HTTPS = "https";
  static final String SCHEME_HTTP = "http";
  static final String KEYSTORE_TYPE_PKCS12 = "pkcs12";
  static final String HTTP_1_1 = "http/1.1";
  public static final int IDLE_TIMEOUT = 15000;

  private Connectors() {
  }

  public static ServerConnector http(final Server server, final int port) {
    Arguments.notNull("server", server);
    Arguments.greaterThan("port", port, 1024);

    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme(SCHEME_HTTP);
    httpConfig.setOutputBufferSize(32768);
    httpConfig.setSendXPoweredBy(true);

    final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
    http.setPort(port);
    http.setIdleTimeout(IDLE_TIMEOUT);

    return http;
  }

  public static ServerConnector https(final Server server, final int port, final String sslCertFile,
    final String sslCertPass) {
    Arguments.notNull("server", server);
    Arguments.greaterThan("port", port, 1024);
    Arguments.notEmpty("sslCertFile", sslCertFile);

    SslContextFactory sslContextFactory = new SslContextFactory();
    sslContextFactory.setKeyStorePath(sslCertFile);
    sslContextFactory.setKeyStoreType(KEYSTORE_TYPE_PKCS12);
    if (!Strings.isNullOrEmpty(sslCertPass)) {
      sslContextFactory.setKeyStorePassword(sslCertPass);
    }

    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme(SCHEME_HTTPS);
    httpConfig.setSecurePort(port);
    httpConfig.setOutputBufferSize(32768);

    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
    httpsConfig.addCustomizer(new SecureRequestCustomizer());

    ServerConnector https =
      new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HTTP_1_1),
        new HttpConnectionFactory(httpsConfig));
    https.setPort(port);
    https.setIdleTimeout(IDLE_TIMEOUT);

    return https;
  }
}
