package vttp2022.paf.mongoWorkshop.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp2022.paf.mongoWorkshop.model.Edit;
import vttp2022.paf.mongoWorkshop.model.Game;
import vttp2022.paf.mongoWorkshop.model.Review;
import vttp2022.paf.mongoWorkshop.repositories.GameRepository;
import vttp2022.paf.mongoWorkshop.services.GameService;

@RestController
@EnableWebMvc
public class GameRESTController {

    @Autowired
    GameService gSvc;

    @Autowired
    GameRepository gRepo;
    

    @GetMapping(path = "/games")
    public ResponseEntity<String> getAllGames(@RequestParam Integer offset, @RequestParam Integer limit){
        List<Game> games = gSvc.getAllGames(offset, limit);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Long ts = System.currentTimeMillis();
        Integer count = gRepo.searchAllGamesCount();
        for(Game g: games){
            arrayBuilder.add(g.toJSON());
        }
        JsonArray arr = arrayBuilder.build();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("games", arr);
        objectBuilder.add("offset", offset);
        objectBuilder.add("limit", limit);
        objectBuilder.add("total", count);
        objectBuilder.add("timestamp", ts);
        JsonObject res =  objectBuilder.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @GetMapping(path = "/games/rank")
    public ResponseEntity<String> getAllGamesRanked(@RequestParam Integer offset, @RequestParam Integer limit){
        if(null==offset){
            offset=25;
        }
        if(null==limit){
            limit=25;
        }
        List<Game> games = gSvc.getAllGamesRanked(offset, limit);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Long ts = System.currentTimeMillis();
        Integer count = gRepo.searchAllGamesCount();
        for(Game g: games){
            arrayBuilder.add(g.toJSON());
        }
        JsonArray arr = arrayBuilder.build();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("games", arr);
        objectBuilder.add("offset", offset);
        objectBuilder.add("limit", limit);
        objectBuilder.add("total", count);
        objectBuilder.add("timestamp", ts);
        JsonObject res =  objectBuilder.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(res.toString());
    }

    @GetMapping(path = "/game/{game_id}")
    public ResponseEntity<String> getGameByID(@PathVariable String game_id){
        Optional<Document> doc = gSvc.getGameByID(game_id);
        if(doc.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("{\"error_mesg\": \"NOT CREATED\"}");
        }
        Game res = Game.createGame(doc.get());
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("game_id", res.getGid());
        job.add("image", res.getImage());
        job.add("name", res.getName());
        job.add("time", res.getTimestamp());
        job.add("url", res.getUrl());
        job.add("rank", res.getRanking());
        job.add("uRate", res.getUsers_rated());
        job.add("year", res.getYear());
        JsonObject jo = job.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jo.toString());
    }

    @PostMapping(path = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addReview(@RequestBody MultiValueMap<String, String> body){
        String name = body.getFirst("name");
        Integer rating = Integer.parseInt(body.getFirst("rating")) ;
        String comments = body.getFirst("comment");
        Integer gid = Integer.parseInt(body.getFirst("gid"));
        if(!gRepo.gidExists(gid)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("{\"error_mesg\": \"GID NOT FOUND\"}");
        }
        gRepo.addReview(name, rating, comments, gid);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"STATUS OK\":\"REVIEW ADDED\"}");
    }

    @PutMapping(path = "/review/{review_id}")
    public ResponseEntity<String> updateReview(@PathVariable String review_id, @RequestBody MultiValueMap<String, String> body){
        String comment = body.getFirst("comment");
        Integer rating = Integer.parseInt(body.getFirst("rating"));
        Integer modded = gRepo.updateReview(comment, rating, review_id);
        if(modded<1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("{\"error_mesg\": \"NOT UPDATED\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"STATUS OK\":\"REVIEW UPDATED\"}");
    }

    @GetMapping(path = "/review/{review_id}")
    public ResponseEntity<String> getReview(@PathVariable String review_id){
        List<Document> docs = gRepo.getReviewByID(review_id);
        if(docs.size()<1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("{\"error_mesg\": \"NOT FOUND\"}");
        }
        Review review = Review.create(docs.get(0));
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("user", review.getUser());
        job.add("comment", review.getComment());
        job.add("date", review.getDate());
        job.add("name", review.getName());
        job.add("gid", review.getGid());
        job.add("rating", review.getRating());
        if(review.getEdits().size()>0){
            job.add("edits", true);
        }else{
            job.add("edits", false);
        }
        JsonObject jo = job.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jo.toString());
    }

    @GetMapping(path = "/review/{review_id}/history")
    public ResponseEntity<String> getReviewHistory(@PathVariable String review_id){
        List<Document> docs = gRepo.getReviewByID(review_id);
        if(docs.size()<1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("{\"error_mesg\": \"NOT FOUND\"}");
        }
        Review review = Review.create(docs.get(0));
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("user", review.getUser());
        job.add("comment", review.getComment());
        job.add("date", review.getDate());
        job.add("name", review.getName());
        job.add("gid", review.getGid());
        job.add("rating", review.getRating());
        if(review.getEdits().size()>0){

            job.add("edits", Edit.toJsonArray(review.getEdits()));//TODO
        }else{
            job.add("edits", "NO EDITS");
        }
        JsonObject jo = job.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jo.toString());
    }

    @GetMapping(path = "/game/{game_id}/reviews")
    public ResponseEntity<String> getBoardGameReviews(@PathVariable Integer game_id){
        List<Document> docs = gRepo.getGameReviews(game_id);
        Document document = docs.get(0);
        Game g = Game.create(document);
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("game_id", g.getGid());
        job.add("name", g.getName());
        job.add("year", g.getYear());
        job.add("rank", g.getRanking());
        job.add("users_rated", g.getUsers_rated());
        job.add("url", g.getUrl());
        job.add("thumbnail", g.getImage());
        job.add("timestamp", LocalTime.now().toString());
        JsonArrayBuilder jab = Json.createArrayBuilder();
        List<Document> reviewsDoc = document.getList("reviews", Document.class);
        for (Document d : reviewsDoc) {
            Review r = Review.create(d);
            jab.add(Review.toJSON(r));
        }
        JsonArray ja = jab.build();
        job.add("reviews", ja);
        JsonObject jo = job.build();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jo.toString());

    }


}
