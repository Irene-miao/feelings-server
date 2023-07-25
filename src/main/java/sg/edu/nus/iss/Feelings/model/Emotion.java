package sg.edu.nus.iss.Feelings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;


import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;


public class Emotion implements Serializable {
   private  String label;
   private  List<Music> music = new ArrayList<Music>();
   private  String[] strategies;
   private String[] test_links;

    
    public Emotion() {
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String[] getStrategies() {
        return strategies;
    }


    public void setStrategies(String[] strategies) {
        this.strategies = strategies;
    }


    public String[] getTest_links() {
        return test_links;
    }


    public void setTest_links(String[] test_links) {
        this.test_links = test_links;
    }


    

    public static JsonObjectBuilder toJSON(Document d){
        JsonArrayBuilder musicArr = Json.createArrayBuilder();
        JsonArrayBuilder strategyArr = Json.createArrayBuilder();
        JsonArrayBuilder linkArr = Json.createArrayBuilder();
        List<Document> music = new ArrayList<>();
        List<String> strategies = new ArrayList<>();
        List<String> links = new ArrayList<>();
        music = d.getList("music", Document.class);
        System.out.printf("object music: %s\n", music);
        strategies = d.getList("strategies", String.class);
        links = d.getList("test_links", String.class);

        for (Document m : music) {
            System.out.printf("music doc: %s\n", m.get("name"));
            JsonObjectBuilder obj = Json.createObjectBuilder()
            .add("name", m.getString("name"))
            .add("youtube", m.getString("youtube") );
            musicArr.add(obj);
           
        }

        for (String s: strategies){
            strategyArr.add(s);
        }

        for(String l : links){
            linkArr.add(l);
        }

        JsonObjectBuilder obj = Json.createObjectBuilder()
        .add("label", d.getString("emotion"))
        .add("music", musicArr)
        .add("strategies", strategyArr)
        .add("test_links", linkArr);

        return obj;
    
    }


    public List<Music> getMusic() {
        return music;
    }


    public void setMusic(List<Music> music) {
        this.music = music;
    }


    @Override
    public String toString() {
        return "Emotion [label=" + label + ", music=" + music + ", strategies=" + Arrays.toString(strategies)
                + ", test_links=" + Arrays.toString(test_links) + "]";
    }


    
}
