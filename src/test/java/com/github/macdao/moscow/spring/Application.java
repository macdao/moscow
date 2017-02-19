package com.github.macdao.moscow.spring;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(path = "/foo", method = RequestMethod.GET)
    public String getFoo(@RequestParam String param) {
        return "bar4";
    }

    @RequestMapping(path = "/property", method = RequestMethod.GET)
    public Map<String, String> getProperty() {
        return ImmutableMap.of("name", "0");
    }
}
