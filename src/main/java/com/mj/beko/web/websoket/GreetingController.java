package com.mj.beko.web.websoket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 */
@Controller
public class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetingsBack")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000);
        return new Greeting("Hello,I'm "+message.getName()+"!");
    }
    @MessageMapping("/helloBack")
    @SendTo("/topic/greetings")
    public Greeting greetingBack(HelloMessage message) throws Exception{
        return new Greeting("I'm fine thank you,I am "+message.getName());
    }
}


