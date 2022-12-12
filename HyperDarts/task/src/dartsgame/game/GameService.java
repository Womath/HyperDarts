package dartsgame.game;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Game getGame(Long id) {
        return gameRepository.getById(id);
    }

    /**
     * Searching for game by username
     * @param name - username
     * @return - returns Map of Game object where user plays
     */
    public Map getGameByName(String name) {
        Optional<Game> gameByPlayer = gameRepository.findGameByPlayerName(name);
        if (gameByPlayer.isPresent()) {
            return objectMapper.convertValue(gameByPlayer.get(), Map.class);
        } else {
            return Collections.emptyMap();
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
        return gameRepository.findGameByPlayerName(name).isPresent();
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

    /**
     * It makes changes in the game according to darts rules. Subtract thrown score, changes game status if needed,
     * handles checkout and bust and at the end changes turn to the other player.
     * @param id - ID of the game
     * @param name - Name of the actual player
     * @param darts - Array of thrown darts
     * @return - returns modified Game object as a Map
     */
    public Map applyThrows(Long id, String name, ThrownDart[] darts) {
        Game currentGame = getGame(id);

        Integer playerScore;
        if (currentGame.getPlayerOne().equals(name)) {
            playerScore = currentGame.getPlayerOneScores();
        } else {
            playerScore = currentGame.getPlayerTwoScores();
        }

        for (ThrownDart dart : darts) {
            if (dart.getScore() != null) {
                playerScore -= dart.getMultiplicator() * dart.getScore();
            }

            if (playerScore == 0 && dart.getMultiplicator() == 2) {
                setPlayerScore(currentGame, name, playerScore);
                currentGame.setGameStatus(name + " wins!");
                gameRepository.save(currentGame);
                return objectMapper.convertValue(currentGame, Map.class);
            } else if (playerScore < 0 || playerScore == 1 || (playerScore == 0 && dart.getMultiplicator() != 2)) {
                setNextPlayer(currentGame, name);
                if (currentGame.getGameStatus().equals("started")) {
                    currentGame.setGameStatus("playing");
                }
                gameRepository.save(currentGame);
                return objectMapper.convertValue(currentGame, Map.class);
            }
        }

        setPlayerScore(currentGame, name, playerScore);
        setNextPlayer(currentGame, name);
        if (currentGame.getGameStatus().equals("started")) {
            currentGame.setGameStatus("playing");
        }
        gameRepository.save(currentGame);
        return objectMapper.convertValue(currentGame, Map.class);
    }


    /**
     * Sets actual player's score
     * @param game - Actual game object
     * @param name - Name of the actual player
     * @param score - Modified score that needs to be applied in the Game
     */
    private void setPlayerScore(Game game, String name, Integer score) {
        if (game.getPlayerOne().equals(name)) {
            game.setPlayerOneScores(score);
        } else {
            game.setPlayerTwoScores(score);
        }
    }

    /**
     * Sets turn to next player
     * @param game - Actual game object
     * @param name - Name of the actual player
     */
    private void setNextPlayer(Game game, String name) {
        if (game.getPlayerOne().equals(name)) {
            game.setTurn(game.getPlayerTwo());
        } else {
            game.setTurn(game.getPlayerOne());
        }
    }

    /**
     * Checks if it is actual player's turn or not
     * @param id - ID of the game
     * @param name - Name of the actual player
     * @return - true or false
     */
    public boolean isTheirTurn(Long id, String name) {
        return (getGame(id).getTurn().equals(name));
    }

    /**
     * Extracts thrown values from input by creating a new ThrownDart object.
     * @param numberOfDart - Order number of the dart (first, second or third)
     * @param thrownDart - String value from input
     * @return - returns an empty ThrownDart object if dart was not thrown (because of winning the game or bust),
     * a ThrownDart object with values or null, if input values are incorrect
     */
    public ThrownDart extractThrow(Integer numberOfDart, String thrownDart) {
        if (thrownDart.equals("none")) {
            return new ThrownDart(numberOfDart);
        }

        String[] values = thrownDart.split(":");
        Integer multiplicator = Integer.parseInt(values[0]);
        Integer score = Integer.parseInt(values[1]);
        if (validateThrowValue(multiplicator, score)) {
            return new ThrownDart(numberOfDart, multiplicator, score);
        } else {
            return null;
        }
    }

    /**
     * Validates the whole set of throws according to the rules of darts
     * @param id - ID of the Game
     * @param name - Name of the actual player
     * @param darts - Array of thrown darts
     * @return - returns true or false
     */
    public boolean validateThrows(Long id, String name, ThrownDart[] darts) {
        for (ThrownDart dart : darts) {
            if (dart == null) {
                return false;
            }
        }

        for (ThrownDart dart : darts) {
            if (dart.getNumberOfDart() == 1 && dart.getScore() == null) {
                return false;
            }
        }

        Game currentGame = getGame(id);
        Integer playerScore;
        if (currentGame.getPlayerOne().equals(name)) {
            playerScore = currentGame.getPlayerOneScores();
        } else {
            playerScore = currentGame.getPlayerTwoScores();
        }

        for (ThrownDart dart : darts) {
            if (playerScore > 1 && (dart.getScore() == null || dart.getMultiplicator() == null)) {
                return false;
            }
            if (playerScore <= 1 && (dart.getScore() != null || dart.getMultiplicator() != null)) {
                return false;
            }

            if (dart.getScore() != null && dart.getMultiplicator() != null) {
                playerScore -= dart.getMultiplicator() * dart.getScore();
            }
        }

        return true;
    }

    /**
     * Validates values of the thrown dart
     * @param multiplicator - 1, 2 or 3 - single, double or treble score
     * @param score - sector of the dart board (1 - 20 and 25)
     * @return - returns true or false
     */
    private boolean validateThrowValue(Integer multiplicator, Integer score) {
        if (multiplicator < 1 || multiplicator > 3) {
            return false;
        }

        if (score >= 0 && score <= 20) {
            return true;
        } else return score == 25 && (multiplicator == 1 || multiplicator == 2);
    }

    /**
     * For status API, if there are no active games, it finds the last finished game (game with the highest ID)
     * @param name - Name of the actual player
     * @return - Map of found Game object
     */
    public Map findLastFinishedGame(String name) {
        List<Game> finishedGameList = gameRepository.findFinishedGamesByPlayerName(name);
        if (finishedGameList.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Collections.reverse(finishedGameList);
            return objectMapper.convertValue(finishedGameList.get(0), Map.class);
        }
    }
}
