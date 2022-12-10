package dartsgame.game;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.objectMapper = new ObjectMapper();
    }

    private Game getGame(Long id) {
        return gameRepository.getById(id);
    }

    public Map getGameByName(String name) {
        Optional<Game> gameByPlayerOne = gameRepository.findGameByPlayerOneName(name);
        if (gameByPlayerOne.isPresent()) {
            return objectMapper.convertValue(gameByPlayerOne.get(), Map.class);
        } else {
            Optional<Game> gameByPlayerTwo = gameRepository.findGameByPlayerTwoName(name);
            return objectMapper.convertValue(gameByPlayerTwo.get(), Map.class);
        }
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Map addNewGame(String name, Integer targetScore) {
        Long id = gameRepository.save(new Game(name, targetScore)).getGameId();
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    public boolean validateTargetScore(Integer targetScore) {
        switch (targetScore) {
            case 101:
            case 301:
            case 501:
                return true;
            default:
                return false;
        }
    }

    public Map joinGame(String name, Long id) {
        Game game = getGame(id);
        if (game.getPlayerOne().equals("")) {
            game.setPlayerOne(name);
        } else if (game.getPlayerTwo().equals("")) {
            game.setPlayerTwo(name);
        }
        gameRepository.save(game);
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    public boolean isInGame(String name) {
        Optional<Game> gameByPlayerOneOptional = gameRepository.findGameByPlayerOneName(name);
        Optional<Game> gameByPlayerTwoOptional = gameRepository.findGameByPlayerTwoName(name);
        return gameByPlayerOneOptional.isPresent() || gameByPlayerTwoOptional.isPresent();
    }

    public boolean isInThisGame(String name, Long id) {
        return getGame(id).getPlayerOne().equals(name);
    }

    public boolean isGame(Long id) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        return gameOptional.isPresent();
    }

    public boolean isAvailableToJoin(Long id) {
        return getGame(id).getGameStatus().equals("created");
    }

    public boolean deleteStudent(Long gameID) {
        if (gameRepository.existsById(gameID)) {
            gameRepository.deleteById(gameID);
            return true;
        } else {
            return false;
        }
    }
}
