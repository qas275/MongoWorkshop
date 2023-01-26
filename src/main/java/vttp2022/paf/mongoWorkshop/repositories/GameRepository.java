package vttp2022.paf.mongoWorkshop.repositories;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class GameRepository {
    
    public static final String BG_C_GAMES = "games";
    public static final String BG_C_REVIEWS = "reviews";

    @Autowired
    MongoTemplate mongoTemplate;

    public Optional<Document> searchGameByID(String id){
        ObjectId OID = new ObjectId(id);
        return Optional.ofNullable(mongoTemplate.findById(OID, Document.class, BG_C_GAMES));
        // MatchOperation findGame = Aggregation.match(Criteria.where("name").regex(name, "i"));
        // SkipOperation skip = Aggregation.skip(offset);
        // LimitOperation limitOps = Aggregation.limit(limit);
        // Aggregation pipeline = Aggregation.newAggregation(findGame, skip, limitOps);
        // AggregationResults<Document> games = mongoTemplate.aggregate(pipeline,BG_C_GAMES , Document.class);
        
        //Criteria c = Criteria.where("name").regex(name, "i").count
    }

    public List<Document> searchAllGames(Integer offset, Integer limit){
        Criteria c = Criteria.where("name").regex(".*.*");
        Query queryGames = Query.query(c).skip(offset).limit(limit);
        queryGames.fields().include("name", "gid");
        List<Document> games = mongoTemplate.find(queryGames, Document.class, BG_C_GAMES);
        return games;
    }

    public Integer searchAllGamesCount(){
        Criteria c = Criteria.where("name").regex(".*.*");
        Query queryGames = Query.query(c).skip(10).limit(10);
        queryGames.fields().include("name", "gid");
        Query queryAllGames = Query.query(c);

        Integer count = (int) mongoTemplate.count(queryAllGames, BG_C_GAMES);
        return count;
    }

    public List<Document> searchAllGamesRanked(Integer offset, Integer limit){
        Criteria c = Criteria.where("name").regex(".*.*");
        Query queryGames = Query.query(c).with(Sort.by(Direction.ASC, "ranking")).skip(offset).limit(limit);
        queryGames.fields().include("name", "gid");
        List<Document> games = mongoTemplate.find(queryGames, Document.class, BG_C_GAMES);
        return games;
    }

    public boolean gidExists(Integer gid){
        Criteria c = Criteria.where("gid").is(gid);
        Query q = Query.query(c);
        Integer count = (int) mongoTemplate.count(q, BG_C_GAMES);
        System.out.println("COUNT>>>>>" +count);
        return count>0;
    }

    public void addReview(String name, Integer rating, String comment, Integer gid){
        Criteria c = Criteria.where("gid").is(gid);
        Query q = Query.query(c);
        Document res = mongoTemplate.find(q, Document.class, BG_C_GAMES).get(0);
        String gameName = res.getString("name");
        Document toInsert = new Document();
        toInsert.put("user", name);
        toInsert.put("rating", rating);
        toInsert.put("comment", comment);
        toInsert.put("ID", gid);
        toInsert.put("posted", LocalTime.now().toString());
        toInsert.put("name", gameName);
        mongoTemplate.insert(toInsert, BG_C_REVIEWS);
    }

    public Integer updateReview(String comment, Integer rating, String reviewID){
        try {
            //need encase within try catch as OID converts string with hexdeci algo, will throw error if invalid hexdeci
            ObjectId OID = new ObjectId(reviewID); 
            Criteria c = Criteria.where("_id").is(OID);
            Query query = Query.query(c);
            Document edits = new Document().append("comment", comment).append("rating", rating).append("posted", LocalTime.now().toString());        
            Update update = new Update().push("edited", edits);
            
            UpdateResult res =  mongoTemplate.updateMulti(query, update, Document.class, BG_C_REVIEWS);
            System.out.println(">>>>>>\n\n\n DOCUMENTS UPDATED >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>         " + res.getModifiedCount());
            return (int) res.getModifiedCount();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Document> getReviewByID(String reviewID){
        ObjectId OID = new ObjectId(reviewID);
        Criteria c = Criteria.where("_id").is(OID);
        Query q = Query.query(c);
        List<Document> res = mongoTemplate.find(q, Document.class, BG_C_REVIEWS);
        return res;
    }

    public List<Document> getGameReviews(Integer gameID){
        MatchOperation matchGID = Aggregation.match(Criteria.where("gid").is(gameID));
        LookupOperation lookup = Aggregation.lookup("reviews", "ID", "gid", "reviews");
        Aggregation pipe = Aggregation.newAggregation(matchGID, lookup);
        AggregationResults<Document> res = mongoTemplate.aggregate(pipe, BG_C_GAMES, Document.class);
        List<Document> resL = new LinkedList<>();
        for (Document document : res) {
            resL.add(document);
        }
        return resL;
    }

    public void getHigh(){
        // LookupOperation look = Aggregation.lookup("comments", "gid", "gid", "Comments")
        // Arrays.asList(new Document("$lookup", new Document("from", "comments")
        //     .append("localField", "_id")
        //     .append("foreignField", "profile_id")
        //     .append("as", "result")
        //     .append("pipeline", Arrays.asList(new Document("$match", 
        //         new Document("new_recommendations", 
        //         new Document("$eq", 
        //         new BsonNull())))))))

    }

}
