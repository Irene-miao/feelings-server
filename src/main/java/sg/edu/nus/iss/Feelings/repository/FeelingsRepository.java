package sg.edu.nus.iss.Feelings.repository;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.result.DeleteResult;

import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.Feelings.model.Emotion;
import sg.edu.nus.iss.Feelings.model.Post;
import sg.edu.nus.iss.Feelings.model.User;


@Repository
public class FeelingsRepository {
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    private static final String INSERT_USER = "insert into users(user_id, username, user_password, email, avatar_img, img_type, reset_password_token) values (?,?,?,?,?,?, ?);";

    private static final String GET_USER_BY_USERNAME = "select * from users where username = ?;";

    private static final String GET_USERNAME_AND_PASSWORD = "select username, user_password from users where username = ?;";

    private static final String GET_USERS_IMAGES = "select user_id, avatar_img, img_type,  username, user_password from users;";

    private static final String GET_USERS = "select  username, user_password from users;";

    private static final String DELETE_USER = "delete from users where username = ?;";

    private static final String GET_USER_BY_EMAIL = "select * from users where email = ?;";
    
    private static final String GET_USER_BY_TOKEN = "select * from users where reset_password_token = ?;";

    private static final String UPDATE_PASSWORD = "update users set  user_password = ?, reset_password_token = null where username = ?;";

    private static final String UPDATE_TOKEN_BY_EMAIL = "update users set reset_password_token = ? where email = ?;";


    
    //  db.emotions.find({ emotion: 'happy' });
    public String getEmotion(String label) {

        
        JsonObjectBuilder obj = null;
        Query query = Query.query(Criteria.where("emotion").is(label));

        List<Document> result = mongoTemplate.find(query, Document.class, "emotion");

       
        for (Document d: result){
            obj = Emotion.toJSON(d);
        }

        String json = obj.build().toString();

        return json;
    }

    //  insert into users(user_id, username, user_password, email, avatar_img, img_type) values ("12345678","Bob","bob","bob@mail.com","[B@7b0fafbf", "image/png");
    public String register(MultipartFile avatar_img, String username, String email, String password) {

       
        UUID uuid = UUID.randomUUID();
        String userId = uuid.toString().substring(0, 8);
       
        String mediaType = avatar_img.getContentType();
        String newPassword = passwordEncoder.encode(password);
        String resetPasswordToken = null;
        try{
         byte[] buffer = avatar_img.getBytes();
        
        jdbcTemplate.update(INSERT_USER, userId, username, newPassword, email, buffer, mediaType, resetPasswordToken);

        }catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return userId;
           
    }

    // select * from users where username = "Bob";
    public Optional<User> login(String username){

            return jdbcTemplate.query(GET_USER_BY_USERNAME, 
            rs -> {
                if (!rs.next()){
                    return Optional.empty();
                }
                return Optional.of(new User(rs.getBytes("avatar_img"), rs.getString("img_type"), rs.getString("username"), rs.getString("user_id"), rs.getString("email")));
            },
            username
            );
    }

     // select username, user_password from users where username = ?
     public Optional<User> findByUsername(String username){

            return jdbcTemplate.query(GET_USERNAME_AND_PASSWORD, 
            rs -> {
                if (!rs.next()){
                    return Optional.empty();
                }
                return Optional.of(new User(rs.getString("username"), rs.getString("user_password")));
            },
            username
            );
    }
    

    // db.posts.insertOne({ user_id: '12345678', post_id: '87654321', title: 'Saturday', content: 'Hello Saturday', date: '2023-06017T22.27.00.566Z' });
    public String post(String userId, String title, String content){
       
        UUID uuid = UUID.randomUUID();
        String postId = uuid.toString().substring(0, 8);
        Date date = new Date();
    
        try {
            Post p = new Post(userId, postId, title, content, date);
            String json = p.toJSON().toString();
            Document d = Document.parse(json);

            if (!mongoTemplate.getCollectionNames().contains("posts")){
			mongoTemplate.createCollection("posts");
		}

            Document doc = mongoTemplate.insert(d, "posts");
            
        }catch(Exception e){
            e.printStackTrace();
        }
       
        return postId;
    }

    // db.posts.find({ post_id: {$exists: true }}).limit(5).skip(0);
    public List<Post> getPosts(Integer limit, Integer offset) {

        List<Post> posts = new ArrayList<>();

        Query query = Query.query(Criteria.where("post_id").exists(true)).limit(limit).skip(offset);

        List<Document> result = mongoTemplate.find(query, Document.class, "posts");

        System.out.println("postsResult: "+ result.toString());
     
        for (Document d : result){
            posts.add(Post.create(d));
        }

        return posts;
    }

    // 
    public List<User> getUsers() {
        
        List<User> users = new ArrayList<>();
        
        final SqlRowSet rs = jdbcTemplate.queryForRowSet(GET_USERS);

        while(rs.next()){

            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("user_password"));
            users.add(user);
        }

        return users;
    }

     public List<User> getImages() throws IOException {
        
        List<User> users = new ArrayList<>();
        
        final SqlRowSet rs = jdbcTemplate.queryForRowSet(GET_USERS_IMAGES);

        while(rs.next()){
         
          Object obj = rs.getObject("avatar_img");
           
            User user = new User();
            user.setUser_id(rs.getString("user_id"));
            user.setImg_type(rs.getString("img_type"));
            user.setAvatar_img((byte[])obj);
            users.add(user);
        }

        return users;
    }

    //  db.posts.deleteOne({ post_id: '1e0e48a0'});
    public Long deletePost(String postId){

        Query query = new Query(Criteria.where("post_id").is(postId));
       DeleteResult result =  mongoTemplate.remove(query, "posts");

       System.out.println(result.getDeletedCount());

       return result.getDeletedCount();
    }


    public  void deleteUser(String username){

        jdbcTemplate.update(DELETE_USER, username);

    }

    // check user's email when click forgot password button
    public Optional<User> getUserByEmail(String email){

        SqlRowSet rs = jdbcTemplate.queryForRowSet(GET_USER_BY_EMAIL, email);
        System.out.println("Repo rs: "+ rs.first());
        if (rs.first()){
            return Optional.of(new User(rs.getString("username"), rs.getString("user_password")));
        } else {
            return Optional.empty();
        }
    }

    // validate token when user click reset password email
    public Optional<User> getUserByToken(String token){

         return jdbcTemplate.query(GET_USER_BY_TOKEN, 
            rs -> {
                if (!rs.next()){
                    return Optional.empty();
                }
                return Optional.of(new User(rs.getBytes("avatar_img"), rs.getString("img_type"), rs.getString("username"), rs.getString("user_id"), rs.getString("email")));
            },
            token
            );
    }

    // set reset_password_token with email
    public int updateResetPasswordToken(String token, String email){
        
        System.out.println("Repotoken:" + token);
        int rows = 0;
        Optional<User> opt = getUserByEmail(email);
        System.out.println("Repo user: "+ opt.isPresent());
        if (opt.isPresent()){
             rows = jdbcTemplate.update(UPDATE_TOKEN_BY_EMAIL, token, email);
        }       
        
            
            System.out.println("rows: " + rows);
          
            return rows;
        
    }

    // find user with reset_password_token
    public Optional<User> getByResetPasswordToken(String token) {
        Optional<User> opt = getUserByToken(token);
        return opt;
    }


    // set new password, reset_password_token to null
    public int updatePassword(User user, String newPassword){
        String encodedPassword = passwordEncoder.encode(newPassword);
        Object[] params = {encodedPassword, user.getUsername()};
        int[] types = {Types.VARCHAR, Types.VARCHAR};

          int rows = jdbcTemplate.update(UPDATE_PASSWORD, params, types);

          System.out.println("rows updated: "+ rows);
        return rows;
   }
 }
