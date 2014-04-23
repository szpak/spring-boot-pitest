package com.company.hello.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.testng.annotations.Test;

public class ConnectorsTest {
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testHttpWithNullServer() {
    Connectors.http(null, 1010);
    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testHttpWithInvalidPort() {
    Connectors.http(mock(Server.class), 1024);
    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
  }

  @Test
  public void testHttp() {
    final ServerConnector http = Connectors.http(mock(Server.class), 1025);
    assertThat(http.isStopped()).isTrue();
    http.shutdown();
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testHttpsWithNullServer() {
    Connectors.https(null, 1010, null, null);
    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testHttpsWithNullCertFile() {
    Connectors.https(mock(Server.class), 1010, null, "ewerk");
    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testHttpsWithEmptyCertFile() {
    Connectors.https(mock(Server.class), 1010, "", "ewerk");
    failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
  }
}
