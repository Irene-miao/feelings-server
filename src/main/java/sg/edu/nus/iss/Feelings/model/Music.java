package sg.edu.nus.iss.Feelings.model;

import java.io.Serializable;

public class Music implements Serializable {
    private String name;
    private String youtube;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getYoutube() {
        return youtube;
    }
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    
    @Override
    public String toString() {
        return "Music [name=" + name + ", youtube=" + youtube + "]";
    }


    
}
