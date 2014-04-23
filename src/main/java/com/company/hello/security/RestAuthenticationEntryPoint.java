package com.company.hello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();

  private final ObjectMapper objectMapper;

  public RestAuthenticationEntryPoint(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(@NotNull final HttpServletRequest request,
    @NotNull final HttpServletResponse response, final AuthenticationException authException)
    throws IOException, ServletException {

    String message = "No authentication exception provided. This is an unexpected error.";
    if (authException != null) {
      message = authException.getMessage();
    }

    HttpError error = new HttpError(UNAUTHORIZED, message);

    final String data = objectMapper.writeValueAsString(error);

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(error.getHttpStatus());
    response.setContentLength(data.length());
    response.getWriter().print(data);
    response.getWriter().flush();
  }
}
