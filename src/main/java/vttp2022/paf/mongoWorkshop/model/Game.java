package vttp2022.paf.mongoWorkshop.model;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Game {
    private String name;
    private Integer gid;
    private Integer year;
    private Integer ranking;
    private Integer users_rated;
    private String image;
    private String timestamp;
    private String url;
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public Integer getRanking() {
        return ranking;
    }
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    public Integer getUsers_rated() {
        return users_rated;
    }
    public void setUsers_rated(Integer users_rated) {
        this.users_rated = users_rated;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    /*
     * year: <Year field>,
ranking: <Rank field>,
average: <Average field>,
users_rated: <Users rated field>,
url: <URL field>,
thumbnail: <Thumbnail field>,
timestamp: <result timestamp>
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getGid() {
        return gid;
    }
    public void setGid(Integer gid) {
        this.gid = gid;
    }


    public static Game create(Document doc){
        Game game = new Game();
        game.setName(doc.getString("name"));
        game.setGid(doc.getInteger("gid"));
        game.setYear(doc.getInteger("year"));
        game.setRanking(doc.getInteger("ranking"));
        game.setUrl(doc.getString("url"));
        game.setImage(doc.getString("image"));
        game.setUsers_rated(doc.getInteger("users_rated"));
        return game;
    }

    public JsonObject toJSON(){
        return Json.createObjectBuilder().add("name", getName()).add("gid", getGid()).build();
    }
    public static Game createGame(Document document) {
        Game game  = new Game();
        game.setGid(document.getInteger("gid"));
        game.setName(document.getString("name"));
        game.setImage(document.getString("image"));
        game.setRanking(document.getInteger("ranking"));
        game.setTimestamp(Integer.toString(((int) System.currentTimeMillis())));
        game.setUrl(document.getString("url"));
        game.setUsers_rated(document.getInteger("users_rated"));
        game.setYear(document.getInteger("year"));
        return game;
    }
}
