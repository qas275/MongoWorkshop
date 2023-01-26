package vttp2022.paf.mongoWorkshop.model;

import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Review {
    /*
     * {
user: <name form field>,
rating: <latest rating>,
comment: <latest comment>,
ID: <game id form field>,
posted: <date>,
name: <The board game’s name as per ID>,
edited: [
{ comment: ..., rating: ..., posted: ... },
{ comment: ..., rating: ..., posted: ... },
{ comment: ..., rating: ..., posted: ... }
],
timestamp: <result timestamp>
}
     */

     private String user;
     private Integer rating;
     private String comment;
     private Integer gid;
     private String date;
     private String name;
     private List<Edit> edits;
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Integer getGid() {
        return gid;
    }
    public void setGid(Integer gid) {
        this.gid = gid;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Edit> getEdits() {
        return edits;
    }
    public void setEdits(List<Edit> edits) {
        this.edits = edits;
    }

    public static Review create(Document doc){
        Review review = new Review();
        review.setComment(doc.getString("comment"));
        review.setDate(doc.getString("posted"));
        review.setEdits(Edit.create(doc));
        review.setGid(doc.getInteger("ID"));
        review.setName(doc.getString("name"));
        review.setRating(doc.getInteger("rating"));
        review.setUser(doc.getString("user"));
        return review;
    }
     
    public static JsonObject toJSON (Review r){
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("comment", r.getComment());
        job.add("date", r.getDate());
        job.add("gid", r.getGid());
        job.add("name", r.getName());
        job.add("rating", r.getRating());
        job.add("user", r.getUser());
        if(r.getEdits().size()>0){
            job.add("edits", true);
        }else{
            job.add("edits", false);
        }
        return job.build();
    }
     
     
}
