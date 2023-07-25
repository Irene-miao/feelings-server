package sg.edu.nus.iss.Feelings.utils;

import org.springframework.stereotype.Component;

@Component
public class SecurityConstant {
    
    public static final String url = "https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app";
    public static final String localUrl = "http://localhost:4200";
    public static final long EXPIRATION_TIME = 432_000_000; // 5 days in milisecs
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADERS = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String FEELINGS_LLC = "Feelings, LLC";
    public static final String FEELINGS_SECURITY  = "Feelings Security jwt";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = { "/login", "/register", "/emotion/**", "/posts", "/images", "/forgot", "/resetpassword/**", "/resetpassword"};
    public static final String TOKEN_SECRET = "feelings";
   // public static final String[] USER_AUTHORITIES = {"user:read", "user/create", "user/delete"};
}
