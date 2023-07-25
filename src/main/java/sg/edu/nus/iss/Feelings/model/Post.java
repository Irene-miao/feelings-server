package sg.edu.nus.iss.Feelings.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;


public class Post implements Serializable {
    private String userId;
    private String postId;
    private String title;
    private String content;
    private Date date;
    
    public Post() {
    }
    

    public Post(String userId, String postId, String title, String content, Date date) {
        this.userId = userId;
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getPostId() {
        return postId;
    }


    public void setPostId(String postId) {
        this.postId = postId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "Post [userId=" + userId + ", postId=" + postId + ", title=" + title + ", content=" + content + ", date="
                + date + "]";
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
        .add("user_id", this.getUserId())
        .add("post_id", this.getPostId())
        .add("title", this.getTitle())
        .add("content", this.getContent())
        .add("date", this.getDate().toInstant().toString())
        .build();
    }


    public static Post create(Document d){
       
        Instant ins = Instant.parse(d.getString("date"));
        Date date = Date.from(ins);
        Post p = new Post();
        p.setUserId(d.getString("user_id"));
        p.setPostId(d.getString("post_id"));
        p.setTitle(d.getString("title"));
        p.setContent(d.getString("content"));
        p.setDate(date);

        return p;
    }


   
}
