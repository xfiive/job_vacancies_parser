package org.parser.parsermail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParserJobsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParserJobsApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner runConsole(@Autowired ParserService parserService) {
//        return s -> parserService.start();
//    }


}
