package dartsgame.game;

import dartsgame.game.history.GameHistory;
import dartsgame.game.history.GameHistoryRepository;
import dartsgame.game.history.GameState;
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
    private final GameHistoryRepository gameHistoryRepository;



    @Autowired
    public GameService(GameRepository gameRepository, GameHistoryRepository gameHistoryRepository) {
        this.gameRepository = gameRepository;
        this.gameHistoryRepository = gameHistoryRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves a game from the repository by its unique identifier.
     * @param id - unique identifier of a game to retrieve
     * @return - a Game object with the specified id or null if no such game exists
     */
    public Game getGame(Long id) {
        return gameRepository.getById(id);
    }

    /**
     * Retrieves a game from the repository by the name of one of its players.
     * @param name - name of the player in the game to retrieve
     * @return - map representation of the found game or an empty map if no such game exists
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
     * Retrieves a game history for a game from repository.
     * @param id - unique identifier of the game to retrieve history for
     * @return - a GameHistory object of the specified game or null if no such game history exists
     */
    public GameHistory getGameHistory(Long id) {
        Optional<GameHistory> gameHistoryOptional = gameHistoryRepository.getGameHistoryById(id);
        return gameHistoryOptional.orElse(null);
    }

    /**
     * Retrieves all the games from repository as a list.
     * @return - a list with all the retrieved games or an empty list if no games exists
     */
    public List<Game> getAllGames() {
        List<Game> list = gameRepository.findAll();
        Collections.reverse(list);
        return list;
    }

    /**
     * Determines if player is in any game.
     * @param name - name of the player to check
     * @return - true if the player is in a game, false otherwise
     */
    public boolean isInGame(String name) {
        return gameRepository.findGameByPlayerName(name).isPresent();
    }

    /**
     * Determines if game with the provided id is created by this player.
     * @param name - name of the player to check
     * @param id - id of the game to check
     * @return - true if player created this game, false otherwise
     */
    public boolean isCreatedByThisPlayer(String name, Long id) {
        return getGame(id).getPlayerOne().equals(name);
    }

    /**
     * Determines if any game exists with provided id.
     * @param id - id of the game to check
     * @return - true if a game exists with provided id, false otherwise
     */
    public boolean isGame(Long id) {
        return gameRepository.findById(id).isPresent();
    }

    /**
     * Determines if a player can join to the game with provided id by checking its status
     * Player can only join to a game with "created" status
     * @param id - id of the game to check
     * @return - true if status of the game is "created", false otherwise
     */
    public boolean isAvailableToJoin(Long id) {
        return getGame(id).getGameStatus().equals("created");
    }

    /**
     * Determines if it is the player's turn
     * @param id - id of the game to check
     * @param name - name of the player to check
     * @return - true if name in turn field is same as the provided player name
     */
    public boolean isTheirTurn(Long id, String name) {
        return (getGame(id).getTurn().equals(name));
    }

    /**
     * Adds new game to the table
     * @param name - name of the game creator player
     * @param targetScore - starting score of the game
     * @return - map representation is the created game
     */
    public Map addNewGame(String name, Integer targetScore) {
        Long id = gameRepository.save(new Game(name, targetScore)).getGameId();
        GameHistory gameHistory = new GameHistory(id);
        gameHistoryRepository.save(gameHistory);
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    /**
     * Adds user to a game and modifies that game's status
     * @param name - name of the joining player
     * @param id - unique identifier of a game
     * @return - map representation of the modified game
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
        addNewGameStatusToExistingGameHistory(game);
        return objectMapper.convertValue(getGame(id), Map.class);
    }

    /**
     * It makes changes in the game according to darts rules. Subtract thrown score, changes game status if needed,
     * handles checkout and bust and at the end changes turn to the other player.
     * @param id - unique identifier of a game
     * @param name - name of the actual player
     * @param darts - array of thrown darts
     * @return - map representation of the modified game
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
                addNewGameStatusToExistingGameHistory(currentGame);
                return objectMapper.convertValue(currentGame, Map.class);
            } else if (playerScore < 0 || playerScore == 1 || (playerScore == 0 && dart.getMultiplicator() != 2)) {
                setNextPlayer(currentGame, name);
                if (currentGame.getGameStatus().equals("started")) {
                    currentGame.setGameStatus("playing");
                }
                gameRepository.save(currentGame);
                addNewGameStatusToExistingGameHistory(currentGame);
                return objectMapper.convertValue(currentGame, Map.class);
            }
        }

        setPlayerScore(currentGame, name, playerScore);
        setNextPlayer(currentGame, name);
        if (currentGame.getGameStatus().equals("started")) {
            currentGame.setGameStatus("playing");
        }
        gameRepository.save(currentGame);
        addNewGameStatusToExistingGameHistory(currentGame);
        return objectMapper.convertValue(currentGame, Map.class);
    }

    /**
     * For status API, if there are no active games, it finds the last finished game (game with the highest ID)
     * @param name - Name of the actual player
     * @return - map representation of the found game or an empty map if such a game not exists
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

    /**
     * Changes the status of a game
     * @param id - unique identifier of a game
     * @param status - provided new status that will replace the previous
     * @return - map representation of the modified game
     */
    public Map changeGameStatus(Long id, String status) {
        Game game = getGame(id);
        game.setGameStatus(status);
        gameRepository.save(game);
        return objectMapper.convertValue(game, Map.class);
    }

    /**
     * Reverts a game to a previous state
     * @param id - unique identifier of a game
     * @param move - the move number of a game to revert to
     * @return map representation of the modified game
     */
    public Map setGameBackToGameState(Long id, Integer move) {
        Game game = getGame(id);
        GameHistory gameHistory = getGameHistory(id);
        GameState gameState = gameHistory.getHistoryList().get(move);

        game.setGameId(gameState.getGameId());
        game.setPlayerOne(gameState.getPlayerOne());
        game.setPlayerTwo(game.getPlayerTwo());
        game.setGameStatus(gameState.getGameStatus());
        game.setPlayerOneScores(gameState.getPlayerOneScores());
        game.setPlayerTwoScores(gameState.getPlayerTwoScores());
        game.setTurn(gameState.getTurn());

        gameHistory.deleteGameStatesAfterRevert(gameState.getMove());

        gameHistoryRepository.save(gameHistory);
        gameRepository.save(game);

        return game.toMap();
    }

    /**
     * Deletes a game from repository.
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
     * Sets actual player's score
     * @param game - actual game to modify
     * @param name - name of the actual player
     * @param score - new score that needs to be applied to the game
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
     * @param game - actual game
     * @param name - name of the actual player
     */
    private void setNextPlayer(Game game, String name) {
        if (game.getPlayerOne().equals(name)) {
            game.setTurn(game.getPlayerTwo());
        } else {
            game.setTurn(game.getPlayerOne());
        }
    }

    /**
     * Adds a new state to the game's history list
     * @param game - actual game that's state has to be saved
     */
    private void addNewGameStatusToExistingGameHistory(Game game) {
        GameHistory gameHistory = getGameHistory(game.getGameId());
                gameHistory.addNewGameState(
                        game.getGameId(),
                        gameHistory.getHistoryList().size(),
                        game.getPlayerOne(),
                        game.getPlayerTwo(),
                        game.getGameStatus(),
                        game.getPlayerOneScores(),
                        game.getPlayerTwoScores(),
                        game.getTurn());
        gameHistoryRepository.save(gameHistory);
    }
}
