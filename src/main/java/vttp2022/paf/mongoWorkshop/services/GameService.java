package vttp2022.paf.mongoWorkshop.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp2022.paf.mongoWorkshop.model.Game;
import vttp2022.paf.mongoWorkshop.repositories.GameRepository;

@Service
public class GameService {
    

    @Autowired
    GameRepository gameRepo;

    public List<Game> getAllGames(Integer offset, Integer limit){
        List<Document> allGamesDoc = gameRepo.searchAllGames(offset, limit);
        List<Game> games = new LinkedList<>();
        for (Document document : allGamesDoc) {
            games.add(Game.create(document));
        }

        return games;

    }

    public List<Game> getAllGamesRanked(Integer offset, Integer limit){
        List<Document> allGamesDoc = gameRepo.searchAllGamesRanked(offset, limit);
        List<Game> games = new LinkedList<>();
        for (Document document : allGamesDoc) {
            games.add(Game.create(document));
        }

        return games;

    }

    public Optional<Document> getGameByID(String ID){
        return gameRepo.searchGameByID(ID);
    }
}
