package sg.edu.nus.iss.Feelings.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static sg.edu.nus.iss.Feelings.utils.SecurityConstant.*;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry){
        
        registry.addMapping("/**").allowedOrigins(url).allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");
    }
}
