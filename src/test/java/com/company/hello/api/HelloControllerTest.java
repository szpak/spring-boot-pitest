package com.company.hello.api;

import com.company.hello.api.HelloController;

public class HelloControllerTest {

  public void testGreeting() {
    HelloController controller = new HelloController();
    controller.greeting();
  }
}
