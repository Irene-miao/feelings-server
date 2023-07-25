package sg.edu.nus.iss.Feelings.controller;



import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.json.Json;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import jakarta.json.JsonReader;
import jakarta.validation.Valid;
import sg.edu.nus.iss.Feelings.model.Post;
import sg.edu.nus.iss.Feelings.model.Reset;
import sg.edu.nus.iss.Feelings.model.User;
import sg.edu.nus.iss.Feelings.model.UserPrincipal;

import sg.edu.nus.iss.Feelings.service.EmailServiceImpl;
import sg.edu.nus.iss.Feelings.service.FeelingsService;
import sg.edu.nus.iss.Feelings.utils.JwtTokenUtil;


import static sg.edu.nus.iss.Feelings.utils.SecurityConstant.*;




@RestController
@RequestMapping
@CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app", maxAge = 3600, allowCredentials = "true")
public class FeelingsController {
    
    private FeelingsService feelingsSvc;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private EmailServiceImpl emailSvc;

    @Autowired
    public FeelingsController(JwtTokenUtil jwtTokenUtil, FeelingsService feelingsSvc,
            AuthenticationManager authenticationManager,EmailServiceImpl emailSvc ) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.feelingsSvc = feelingsSvc;
        this.authenticationManager = authenticationManager;
        this.emailSvc = emailSvc;
    }


    @GetMapping(path="/emotion/{label}")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> getEmotion(@PathVariable String label) {

        String emotion = "";
        String error = "Emotion %s not found" + label;
        System.out.println("error: "+ error);
        try {
            emotion = feelingsSvc.getEmotion(label);
            System.out.printf("getEmotion: %s\n", emotion);
        }catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Json.createObjectBuilder()
            .add("error", error)
            .build().toString());
        }


        return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(emotion);
    }

    @PostMapping(path="/register", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> register(@RequestPart MultipartFile avatar_img, @RequestPart String username, @RequestPart String email, @RequestPart String password) {
        
       String error = "Register user %s failure" + username;
        String userId = "";
        try {
            userId = feelingsSvc.register(avatar_img, username, email, password);
        }catch(Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Json.createObjectBuilder()
            .add("error", error).build()
            .toString());
        }

        return ResponseEntity.status(HttpStatus.OK)
    .contentType(MediaType.APPLICATION_JSON)
    .body(Json.createObjectBuilder()
    .add("message", userId)
    .build().toString());
    }
    


    @PostMapping(path="/login", consumes=MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true", exposedHeaders = "Jwt-Token")
    public ResponseEntity<String> login(@RequestBody User user) {

        
    System.out.println("username: "+ user.getUsername());
    
       User loginUser = null;
        Optional<User> opt = null;
HttpHeaders jwtHeader = null;
      
      
        try {
            authenticate(user.getUsername(), user.getPassword());
            opt = feelingsSvc.login(user.getUsername());
            if (opt.isPresent()){
                loginUser = opt.get();
            }
            System.out.println("loginUser: "+ loginUser);
            UserPrincipal userPrincipal = new UserPrincipal(loginUser);
             jwtHeader = getJwtHeader(userPrincipal);

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        String json = loginUser.toJson().toString();

         return ResponseEntity.ok()
         .headers(jwtHeader)
         .contentType(MediaType.APPLICATION_JSON)
            .body(json);
          
    }

    
    @PostMapping(path="/logout")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<?> logout(){
        JsonObject obj = Json.createObjectBuilder()
        .add("message", "You have been signed out").build();
        String json = obj.toString();


        return ResponseEntity.ok()
        .body(json);

    }

    @PostMapping(path="/post")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> createPost(@RequestBody String post) {
        
        
        JsonReader r = Json.createReader(new StringReader(post));
		JsonObject o = r.readObject();
        String userId = o.getString("user_id");
        String title = o.getString("title");
        String content = o.getString("content");
        String postId = "";
        String error = "Post title %s failure" + title;
        try {
            postId = this.feelingsSvc.post(userId, title, content);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Json.createObjectBuilder()
            .add("error", error).build()
            .toString());
        }

    
         return ResponseEntity.status(HttpStatus.OK)
    .contentType(MediaType.APPLICATION_JSON)
    .body(Json.createObjectBuilder()
    .add("postId", postId)
    .build().toString()
    );

    }

    @GetMapping(path="/posts")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> getPosts(@RequestParam(required=false) Integer limit, @RequestParam(required=false) Integer offset){

        Integer defaultLimit = limit != null ? limit : 5;
        Integer defaultOffset = offset != null ? offset : 0;
        JsonArrayBuilder arr = Json.createArrayBuilder();
        List<Post> posts= new ArrayList<>();

        try {
           posts =  feelingsSvc.getPosts(defaultLimit, defaultOffset);
           
           for (Post p : posts) {
            arr.add(p.toJSON());
           }
        }catch(Exception e){
            e.printStackTrace();
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Json.createObjectBuilder()
            .add("error", e.getMessage()).build()
            .toString());
        }

        String json = arr.build().toString();
    
         return ResponseEntity.status(HttpStatus.OK)
    .contentType(MediaType.APPLICATION_JSON)
    .body(Json.createObjectBuilder()
    .add("posts", json)
    .build().toString()
    );
    
    }

     @GetMapping(path="/images")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> getImages() {

        JsonArrayBuilder arr = Json.createArrayBuilder();
        List<User> users = new ArrayList<>();
        try {
            users = feelingsSvc.getImages();
           
            for (User u : users){
                arr.add(u.toJSON());
            }

        }catch(IOException e){
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Json.createObjectBuilder()
            .add("error", e.getMessage()).build()
            .toString());
        }

        String json = arr.build().toString();
     

         return ResponseEntity.status(HttpStatus.OK)
    .contentType(MediaType.APPLICATION_JSON)
    .body(Json.createObjectBuilder()
    .add("users", json)
    .build().toString()
    );

    }

    @DeleteMapping(path="/posts/{postId}")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> deletePost(@PathVariable String postId){

        Long deleteCount = null;
        try {
          deleteCount =  feelingsSvc.deletePost(postId);
        }catch(Exception e){
            e.printStackTrace();
        }

      

        return ResponseEntity.ok().body("{}");
    }

    @DeleteMapping(path="/users/{username}")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> deleteUser(@PathVariable String username){

       
      
        try {
         feelingsSvc.deleteUser(username);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Json.createObjectBuilder()
                .add("error", "Failure in deleting user")
                .build().toString()
            );
        }

        return ResponseEntity.ok().body(Json.createObjectBuilder()
        .add("message", "Success in deleting user")
        .build().toString());
    }

    @PostMapping(path="/forgot")
    @CrossOrigin(origins="https://feelings-client-u9gohuf7k-irene-lee-livecomsg.vercel.app" , maxAge = 3600, allowCredentials = "true")
    public ResponseEntity<String> sendEmail(@RequestBody String json){

        JsonReader r = Json.createReader(new StringReader(json));
        JsonObject obj = r.readObject();
        String email = obj.getString("email");
        System.out.println("email: "+ obj.getString("email"));

    String token = UUID.randomUUID().toString().substring(0, 8);
    System.out.println("httpToken: "+ token);
int rows = 0;
   String status = "";
      
       try {
        rows =  feelingsSvc.updateResetPasswordToken(token, email);
       if (rows > 0){
        status = emailSvc.sendMailWithLink(email, token);
       }
         
       }catch(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(500).body(
                Json.createObjectBuilder()
                .add("error", e.getMessage())
                .build().toString()
            );
       }

       System.out.println("status: "+ status);
       return ResponseEntity.ok().body(Json.createObjectBuilder()
        .add("message", status)
        .build().toString());
    }

    @GetMapping(path="/resetpassword/{token}")
    public ModelAndView getToken( @PathVariable String token){

        Reset reset = new Reset();
        reset.setToken(token);
        ModelAndView mv = new ModelAndView();
        mv.addObject("reset", reset);
        Optional<User> opt = feelingsSvc.getByResetPasswordToken(token);
        if (opt.isPresent()){
            mv.setViewName("reset");
        }  else {
            mv.setStatus(HttpStatus.BAD_REQUEST);
            return mv;
        }
        mv.setStatus(HttpStatus.OK);
        return mv;

    }

    @PostMapping(path="/resetpassword")
    public ModelAndView resetPassword(@Valid @ModelAttribute Reset reset, BindingResult result ){

        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
           mv.setViewName("reset");;
           return mv;
        }
        

        Optional<User> opt = null;
        User u = null;
        int rows = 0;
       try {
        opt = feelingsSvc.getByResetPasswordToken(reset.getToken());
        if (opt.isPresent()){
            u = opt.get();
            rows = feelingsSvc.updatePassword(u, reset.getPassword());
        }
       }catch(Exception e){
        e.printStackTrace();
        mv.setStatus(HttpStatus.BAD_REQUEST);
        return mv;
       }

       if (rows > 0){
        mv.setViewName("success");
        mv.setStatus(HttpStatus.OK);
       }
      
       return mv;
    }





private void authenticate(String username, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal user){
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADERS, jwtTokenUtil.generateJwtToken(user));
        return headers;
    }


}
