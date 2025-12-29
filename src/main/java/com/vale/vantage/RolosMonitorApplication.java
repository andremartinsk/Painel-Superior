
package com.vale.vantage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RolosMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(RolosMonitorApplication.class, args);
    }
}
