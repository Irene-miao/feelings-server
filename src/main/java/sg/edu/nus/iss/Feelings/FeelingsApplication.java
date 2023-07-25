package sg.edu.nus.iss.Feelings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@SpringBootApplication
public class FeelingsApplication {



	public static void main(String[] args) {
		SpringApplication.run(FeelingsApplication.class, args);
	}

	 @Bean
    public PasswordEncoder passwordencoder(){
        return new BCryptPasswordEncoder();
    }

	
	
}
