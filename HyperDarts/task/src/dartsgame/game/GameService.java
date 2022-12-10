package dartsgame.game;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Methods working between requests and games database table
 */
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Inner function to get game by game ID
     * @param id - ID of the searched game
     * @return - returns a Game object
     */
    private Game getGame(Long id) {
        return gameRepository.getById(id);
    }

    /**
     * Searching for game by username
     * @param name - username
     * @return - returns Map of Game object where user plays
     */
    public Map getGameByName(String name) {
        Optional<Game> gameByPlayerOne = gameRepository.findGameByPlayerOneName(name);
        if (gameByPlayerOne.isPresent()) {
            return objectMapper.convertValue(gameByPlayerOne.get(), Map.class);
        } else {
            Optional<Game> gameByPlayerTwo = gameRepository.findGameByPlayerTwoName(name);
            return objectMapper.convertValue(gameByPlayerTwo.get(), Map.class);
        }
    }

    /**
     * Lists all the games in table
     * @return - returns List of Game objects
     */
    public List<Game> getAllGames() {
        List<Game> list = gameRepository.findAll();
        Collections.reverse(list);
        return list;
    }

    /**
     * Adds new game to the table
     * @param name - username
     * @param targetScore - starting score of the game
     * @return - returns Map of created new Game object
     */
    public Map addNewGame(String name, Integer targetScore) {
        Long id = gameRepository.save(new Game(name, targetScore)).getGameId();
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    /**
     * Validating target score according to rules of Darts
     * @param targetScore - starting score of the game
     * @return - true or false
     */
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

    /**
     * Adds user to a game and modifies that game's status
     * @param name - username
     * @param id - id of the Game
     * @return - returns Map of modified Game object
     */
    public Map joinGame(String name, Long id) {
        Game game = getGame(id);
        if (game.getPlayerOne().equals("")) {
            game.setPlayerOne(name);
        } else if (game.getPlayerTwo().equals("")) {
            game.setPlayerTwo(name);
        }
        game.setGameStatus("started");
        gameRepository.save(game);
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    /**
     * Checks if user is in any game or not
     * @param name - username
     * @return - true or false
     */
    public boolean isInGame(String name) {
        Optional<Game> gameByPlayerOneOptional = gameRepository.findGameByPlayerOneName(name);
        Optional<Game> gameByPlayerTwoOptional = gameRepository.findGameByPlayerTwoName(name);
        return gameByPlayerOneOptional.isPresent() || gameByPlayerTwoOptional.isPresent();
    }

    /**
     * Checks if user created that game or not
     * @param name - username
     * @param id - ID of the Game
     * @return - true or false
     */
    public boolean isInThisGame(String name, Long id) {
        return getGame(id).getPlayerOne().equals(name);
    }

    /**
     * Checks if any game exists with that ID
     * @param id - ID of the game
     * @return - true or false
     */
    public boolean isGame(Long id) {
        return gameRepository.findById(id).isPresent();
    }

    /**
     * Checks status os the game
     * Player can only join to a game with "created" status
     * @param id - ID of the game
     * @return - true or false
     */
    public boolean isAvailableToJoin(Long id) {
        return getGame(id).getGameStatus().equals("created");
    }

    /**
     * Deletes a game by ID
     * @param gameID - ID of the game
     * @return - true or false
     */
    public boolean deleteGame(Long gameID) {
        if (gameRepository.existsById(gameID)) {
            gameRepository.deleteById(gameID);
            return true;
        } else {
            return false;
        }
    }
}
