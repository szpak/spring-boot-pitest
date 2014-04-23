package com.company.hello;

import com.company.hello.security.SecurityConfiguration;
import com.company.hello.web.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@EnableAutoConfiguration(
  exclude = {MessageSourceAutoConfiguration.class, BatchAutoConfiguration.class})
@Import({WebConfiguration.class, SecurityConfiguration.class})
public class Application {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    application.setShowBanner(false);
    application.setHeadless(true);
    application.setRegisterShutdownHook(true);
    application.setWebEnvironment(true);
    application.setLogStartupInfo(true);
    application.run(args);
  }
}