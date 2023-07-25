package sg.edu.nus.iss.Feelings.utils;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static sg.edu.nus.iss.Feelings.utils.SecurityConstant.*;


@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    
 

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


     
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        
        if (req.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)){
          res.setStatus(HttpStatus.OK.value());
        } else {
          String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
          if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)){
            chain.doFilter(req, res);
            return;
          }

          // retrieve token without "Bearer "
          String token = authorizationHeader.substring(TOKEN_PREFIX.length());
          String username = jwtTokenUtil.getSubject(token);

          if (jwtTokenUtil.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null){
            //List<GrantedAuthority> authorities = jwtTokenUtil.getAuthorities(token);
            Authentication authentication = jwtTokenUtil.getAuthentication(username, null, req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
          } else {
            SecurityContextHolder.clearContext();
          }
        } 
        chain.doFilter(req, res);
    }
      
      
}


