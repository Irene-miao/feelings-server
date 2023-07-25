package sg.edu.nus.iss.Feelings.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import sg.edu.nus.iss.Feelings.model.UserPrincipal;

import static sg.edu.nus.iss.Feelings.utils.SecurityConstant.*;


@Component
public class JwtTokenUtil{
    
     private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

     
  @Value("${jwt.secret}")
  private String jwtSecret;


  public String generateJwtToken(UserPrincipal user) {
   return JWT.create().withIssuer(FEELINGS_LLC).withAudience(FEELINGS_SECURITY)
   .withIssuedAt(new Date()).withSubject(user.getUsername())
   .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
   .sign(Algorithm.HMAC512(TOKEN_SECRET.getBytes()));
  }


    /*private String[] getClaimsFromUser(UserPrincipal user){
      List<String> authorities = new ArrayList<>();
      for (GrantedAuthority grant : user.getAuthorities()){
        authorities.add(grant.getAuthority());
      }
      return authorities.toArray(new String[0]);
    }*/


    /*public List<GrantedAuthority> getAuthorities(String token){
      String[] claims = getClaimsFromToken(token);
      return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }*/

   /*  private String[] getClaimsFromToken(String token){
      JWTVerifier verifier = getJWTVerifier();
      return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }*/


    private JWTVerifier getJWTVerifier(){
      JWTVerifier verifier;
      try {
        Algorithm algorithm = Algorithm.HMAC512(TOKEN_SECRET);
        verifier = JWT.require(algorithm).withIssuer(FEELINGS_LLC).build();
      }catch(JWTVerificationException e){
        logger.info(e.getMessage());
        throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
      }
      return verifier;
    }



 public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest req){
    UsernamePasswordAuthenticationToken userPasswordAuthToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
    userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
    return userPasswordAuthToken;
 }



 public boolean isTokenValid(String username, String token){
  JWTVerifier verifier = getJWTVerifier();
  return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
 }

public String getSubject(String token){
  JWTVerifier verifier = getJWTVerifier();
  return verifier.verify(token).getSubject();
}


 public boolean isTokenExpired(JWTVerifier verifier, String token){
  Date expiration = verifier.verify(token).getExpiresAt();
  return expiration.before(new Date());
 }
}
