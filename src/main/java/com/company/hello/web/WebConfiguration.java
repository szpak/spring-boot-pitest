package com.company.hello.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.collect.Lists;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

  public static final String CONTEXT_PATH = "/dashboard";

  public static final int NO_CACHE = 0;

  /**
   * The following command may be used to create a valid .p12 self signed certificate:
   *
   * <username>keytool -genkey -alias dashboard-server -storetype PKCS12 -keyalg RSA -keysize 2048
   * -keystore server.p12 -validity 3650</username>
   *
   * @return The servlet container factory for creating the embedded Jetty server with SSL enabled
   */
  @Bean
  public JettyEmbeddedServletContainerFactory containerFactory(
    @Value("${jetty.port}") final int jettyPort, @Value("${ssl.enabled}") final boolean sslEnabled,
    @Value("${ssl.cert.file}") final String sslCertFile,
    @Value("${ssl.cert.pass}") final String sslCertPass) {
    return new JettyEmbeddedServletContainerFactory() {
      @Override
      protected JettyEmbeddedServletContainer getJettyEmbeddedServletContainer(
        final Server server) {
        LOG.info("SSL enabled: {}", sslEnabled);

        ServerConnector connector;
        if (sslEnabled) {
          connector = Connectors.https(server, jettyPort, sslCertFile, sslCertPass);
        } else {
          connector = Connectors.http(server, jettyPort);
        }
        server.setConnectors(new Connector[] {connector});
        server.setStopAtShutdown(true);

        return super.getJettyEmbeddedServletContainer(server);
      }
    };
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer(
    @Value("${authentication.session.timeout.min}") final int sessionTimeout) {
    return container -> {
      container.setRegisterJspServlet(false);
      container.setSessionTimeout(sessionTimeout);
      container.setContextPath(CONTEXT_PATH);
    };
  }

  @Bean
  public ObjectMapper jacksonObjectMapper() {
    //Hibernate4Module hibernate4Module = new Hibernate4Module();
    //hibernate4Module.enable(
    //  Hibernate4Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    //mapper.registerModule(hibernate4Module);
    mapper.registerModule(new JSR310Module());
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

    mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    mapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
    mapper.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    return mapper;
  }

  @Bean
  public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
    return converter;
  }

  @Bean
  public StringHttpMessageConverter stringConverter() {
    return new StringHttpMessageConverter(Charset.forName("UTF-8"));
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jacksonConverter(jacksonObjectMapper()));
    converters.add(stringConverter());
  }

  @Override
  public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
    final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(10);
    taskExecutor.setMaxPoolSize(30);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.initialize();

    configurer.setTaskExecutor(taskExecutor);
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
      .addResourceLocations("classpath:/frontend/**")
      .setCachePeriod(NO_CACHE);
  }
}
