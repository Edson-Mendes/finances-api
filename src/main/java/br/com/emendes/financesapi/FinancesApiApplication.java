package br.com.emendes.financesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class FinancesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancesApiApplication.class, args);
	}

}
