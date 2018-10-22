package com.rbkmoney.fistful.magista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.rbkmoney.fistful.magista", "com.rbkmoney.dbinit"})
public class FistfulMagistaApplication {

    public static void main(String... args) {
        SpringApplication.run(FistfulMagistaApplication.class, args);
    }

}
