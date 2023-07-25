package sg.edu.nus.iss.Feelings.model;

import java.io.Serializable;

public class Reset implements Serializable {
    private String token;
    private String password;

    

    public Reset() {
    }

    
    public Reset(String token, String password) {
        this.token = token;
        this.password = password;
    }


    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Reset [token=" + token + ", password=" + password + "]";
    }

    
}
