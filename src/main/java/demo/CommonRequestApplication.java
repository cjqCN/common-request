package demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CommonRequestApplication {


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CommonRequestApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


}
