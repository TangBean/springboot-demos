package org.tangbean.errorcodedemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class ErrorCodeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrorCodeDemoApplication.class, args);
    }

}
