package sg.edu.nus.iss.Feelings.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import sg.edu.nus.iss.Feelings.service.UserDetailsServiceImpl;
import sg.edu.nus.iss.Feelings.utils.JwtAuthenticationEntryPoint;
import sg.edu.nus.iss.Feelings.utils.AuthorizationFilter;
import sg.edu.nus.iss.Feelings.utils.JwtAccessDeniedHandler;

import static sg.edu.nus.iss.Feelings.utils.SecurityConstant.*;

import java.util.Arrays;



@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    
    private UserDetailsServiceImpl userDetailsService;
    private PasswordEncoder passwordEncoder;
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private AuthorizationFilter authorizationFilter;
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
   

    public WebSecurityConfig( @Qualifier("userDetailsService")UserDetailsServiceImpl userDetailsService,
             PasswordEncoder passwordEncoder,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, AuthorizationFilter authorizationFilter,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) {
       
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.authorizationFilter = authorizationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
    }
  
     @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // not track any login users
            .authorizeHttpRequests((auth) -> auth
            .requestMatchers(PUBLIC_URLS).permitAll()
            .anyRequest().authenticated()
            )

            /* .formLogin(formLogin -> {
                formLogin.loginPage("")
                .loginProcessingUrl("/api/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .permitAll();
            })
            .logout(logout -> {
                logout.permitAll();
            })*/
           .exceptionHandling(exceptionHandling -> exceptionHandling
           .accessDeniedHandler(jwtAccessDeniedHandler)
           .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

   /* private AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, 
        HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
            System.out.print("authentication"+ authentication.getPrincipal());
            UserDetails userdetails = (UserDetails) authentication.getPrincipal();
            String username = userdetails.getUsername(); 
            Optional<User> opt = feelingsRepo.login(username);
            User user = opt.get();
               // Convert blob to base64 string to show as image in Angular
        StringBuilder strBdr = new StringBuilder();
		strBdr.append("data:").append(user.getImg_type()).append(";base64,");
		byte[] buff = user.getAvatar_img();
		String b64 = Base64.getEncoder().encodeToString(buff);
		strBdr.append(b64);
		String imageData = strBdr.toString();

        System.out.println("userdetails: "+ userdetails);
       
            String jwt = jwtTokenUtil.generateJwt(userdetails);
            JsonObjectBuilder obj = Json.createObjectBuilder()
            .add("username", user.getUsername())
            .add("user_id", user.getUser_id())
            .add("avatar_img", imageData);
            String json = obj.build().toString();
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setPath("/api");
            cookie.setMaxAge(24*60*60);
            cookie.setHttpOnly(true);
            httpServletResponse.addCookie(cookie);
            httpServletResponse.getWriter().append(json);
            httpServletResponse.setStatus(200);
            }
        };
    }

     private AuthenticationFailureHandler failureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, 
        HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
            httpServletResponse.getWriter().append("Authentication failure");
            httpServletResponse.setStatus(401);
            }
        };
    }*/

    
    

}


