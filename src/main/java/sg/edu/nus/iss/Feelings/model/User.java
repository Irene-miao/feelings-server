package sg.edu.nus.iss.Feelings.model;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;


import java.util.Objects;


import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User implements Serializable{
    private byte[] avatar_img;
    private String img_type;
    private String user_id;
    private String username;
    private String password;
    private String email;
    private String reset_password_token;
    
    
    public User() {
    }
    
   
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public User(byte[] avatar_img, String img_type, String username, String user_id, String email) {
        this.avatar_img = avatar_img;
        this.img_type = img_type;
        this.username = username;
        this.user_id = user_id;
        this.email = email;
    }

    


    public User(byte[] avatar_img, String img_type, String user_id, String username,  String email,
            String reset_password_token) {
        this.avatar_img = avatar_img;
        this.img_type = img_type;
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.reset_password_token = reset_password_token;
    }


    public byte[] getAvatar_img() {
        return avatar_img;
    }


    public void setAvatar_img(byte[] avatar_img) {
        this.avatar_img = avatar_img;
    }


    public String getImg_type() {
        return img_type;
    }


    public void setImg_type(String img_type) {
        this.img_type = img_type;
    }


    public String getUser_id() {
        return user_id;
    }


    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }



    @Override
    public String toString() {
        return "User [avatar_img=" + Arrays.toString(avatar_img) + ", img_type=" + img_type + ", user_id=" + user_id
                + ", username=" + username + ", password=" + password + ", email=" + email + ", resetPasswordToken="
                + reset_password_token + "]";
    }


    public JsonObject toJSON() {
        // Convert blob to base64 string to show as image in Angular
        StringBuilder strBdr = new StringBuilder();
		strBdr.append("data:").append(this.getImg_type()).append(";base64,");
		byte[] buff = this.getAvatar_img();
		String b64 = Base64.getEncoder().encodeToString(buff);
		strBdr.append(b64);
		String imageData = strBdr.toString();

        
        return Json.createObjectBuilder()
        .add("user_id", this.getUser_id())
        .add("avatar_img", imageData)
        .build();
    }

public JsonObject toJson() {
        // Convert blob to base64 string to show as image in Angular
        StringBuilder strBdr = new StringBuilder();
		strBdr.append("data:").append(this.getImg_type()).append(";base64,");
		byte[] buff = this.getAvatar_img();
		String b64 = Base64.getEncoder().encodeToString(buff);
		strBdr.append(b64);
		String imageData = strBdr.toString();

        
        return Json.createObjectBuilder()
        .add("user_id", this.getUser_id())
        .add("avatar_img", imageData)
        .add("username", this.getUsername())
        .add("email", this.getEmail())
        .build();
    }


@Override
public boolean equals(Object o) {
  if (this == o)
    return true;
  if (o == null || getClass() != o.getClass())
    return false;
  User user = (User) o;
  return Objects.equals(user_id, user.user_id);
}


public String getResetPasswordToken() {
    return reset_password_token;
}


public void setResetPasswordToken(String resetPasswordToken) {
    this.reset_password_token = resetPasswordToken;
}


}
