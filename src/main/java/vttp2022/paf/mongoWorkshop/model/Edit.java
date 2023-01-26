package vttp2022.paf.mongoWorkshop.model;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Edit {
    private String comment;
    private Integer rating;
    private String posted;
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getPosted() {
        return posted;
    }
    public void setPosted(String posted) {
        this.posted = posted;
    }

    public static List<Edit> create(Document doc){
        List<Document> docs = doc.getList("edited", Document.class);
        List<Edit> edits = new LinkedList<>();
        for(Document d: docs){
            Edit edit = new Edit();
            edit.setComment(d.getString("comment"));
            edit.setPosted(d.getString("posted"));
            edit.setRating(d.getInteger("rating"));
            edits.add(edit);
        }
        return edits;

    }

    public JsonObject toJsonObject(){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("comment", getComment());
        objectBuilder.add("posted", getPosted());
        objectBuilder.add("rating", getRating());
        JsonObject res = objectBuilder.build();
        return res;

    }
    public static JsonArray toJsonArray(List<Edit> edits){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(Edit e:edits){
            arrayBuilder.add(e.toJsonObject());
        }
        return arrayBuilder.build();
    }

}
