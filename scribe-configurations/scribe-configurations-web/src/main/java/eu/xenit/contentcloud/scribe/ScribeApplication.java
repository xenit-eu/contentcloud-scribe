package eu.xenit.contentcloud.scribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ScribeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScribeApplication.class, args);
	}

}
