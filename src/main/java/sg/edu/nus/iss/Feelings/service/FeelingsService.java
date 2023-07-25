package sg.edu.nus.iss.Feelings.service;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import sg.edu.nus.iss.Feelings.model.Post;
import sg.edu.nus.iss.Feelings.model.User;
import sg.edu.nus.iss.Feelings.repository.FeelingsRepository;

@Service
public class FeelingsService {
    
 

    @Autowired
    private FeelingsRepository feelingsRepo;

    public String getEmotion(String label){

        return feelingsRepo.getEmotion(label);
    }

    @Transactional(rollbackFor = IOException.class)
    public String register(MultipartFile avatar_img, String username, String email, String password) throws IOException{
        return  feelingsRepo.register(avatar_img, username, email, password);
    }


    
    public String post(String userId, String title, String content){
        return feelingsRepo.post(userId, title, content);
    }

    public List<Post> getPosts(Integer limit, Integer offset) {
        return feelingsRepo.getPosts(limit, offset);
    }

    public List<User> getImages() throws IOException {
        return feelingsRepo.getImages();
    }

    public Optional<User> login(String username){
        return feelingsRepo.login(username);
    }
   
    public Long deletePost(String postId){
        return feelingsRepo.deletePost(postId);
    }

    public void deleteUser(String username){
        feelingsRepo.deleteUser(username);
    }

    public int updateResetPasswordToken(String token, String email){
       return feelingsRepo.updateResetPasswordToken(token, email);
    }

    public Optional<User> getByResetPasswordToken(String token){
        return feelingsRepo.getByResetPasswordToken(token);
    }

    public int updatePassword(User user, String newPassword){
       return feelingsRepo.updatePassword(user, newPassword);
    }
}
