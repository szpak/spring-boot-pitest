package com.company.hello.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api", produces = "text/plain")
@RestController
public class HelloController {

  @RequestMapping("/greeting")
  public String greeting() {
    return "Greetings from Spring Boot!";
  }
}
