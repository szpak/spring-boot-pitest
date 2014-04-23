package com.company.hello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.validation.constraints.NotNull;

@Configuration
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  static final String X_AUTH_TOKEN = "X-Auth-Token";

  static final String AUTHENTICATION_ENABLED = "authentication.enabled";

  //@Bean
  //public AuthenticationManager authenticationManager(final AgentRepository agentRepository,
  //  final UserRepository userRepository,
  //  @Value("${authentication.session.timeout.min}") final int tokenTimeoutMinutes) {
  //  return new TokenBasedAuthenticationManager(agentRepository, userRepository,
  //    tokenTimeoutMinutes);
  //}

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(final ObjectMapper objectMapper) {
    return new RestAuthenticationEntryPoint(objectMapper);
  }

  @Bean
  public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter(
    final AuthenticationManager authenticationManager) {
    RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
    filter.setAuthenticationManager(authenticationManager);
    filter.setExceptionIfHeaderMissing(false);
    filter.setPrincipalRequestHeader(X_AUTH_TOKEN);
    filter.setInvalidateSessionOnPrincipalChange(true);
    filter.setCheckForPrincipalChanges(false);
    filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
    return filter;
  }

  //@Bean
  //public TokenRenewalFilter tokenRenewalFilter() {
  //  return new TokenRenewalFilter(X_AUTH_TOKEN);
  //}

  //@Override
  //public void configure(WebSecurity web) throws Exception {
  //  web.ignoring().antMatchers("/frontend/**");
  //}

  /**
   * Configures the HTTP filter chain depending on configuration settings.
   *
   * Note that this exception is thrown in spring security headerAuthenticationFilter chain and will
   * not be logged as error. Instead the ExceptionTranslationFilter will handle it and clear the
   * security context. Enabling DEBUG logging for 'org.springframework.security' will help
   * understanding headerAuthenticationFilter chain
   */
  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    AuthenticationEntryPoint authenticationEntryPoint =
      fromContext(http, AuthenticationEntryPoint.class);

    //@formatter:off
    http
      .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
      .and()
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
        .anonymous()
      .and()
        .headers().disable()
        .requestCache().disable()
        .servletApi().disable()
        .x509().disable()
        .csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable();
    //@formatter:on

    http.authorizeRequests().antMatchers("/**").permitAll();
  }

  private <T> T fromContext(@NotNull final HttpSecurity http,
    @NotNull final Class<T> requiredType) {
    ApplicationContext ctx = context(http);
    return ctx.getBean(requiredType);
  }

  private ApplicationContext context(final HttpSecurity http) {
    //noinspection SuspiciousMethodCalls
    return (ApplicationContext) http.getSharedObjects().get(ApplicationContext.class);
  }
}
