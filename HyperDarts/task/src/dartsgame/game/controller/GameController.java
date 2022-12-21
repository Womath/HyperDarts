package dartsgame.game.controller;

import dartsgame.game.service.*;
import dartsgame.game.persistance.dao.Game;
import dartsgame.game.persistance.dao.GameHistory;
import dartsgame.game.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final Validator validator;

    @Autowired
    public GameController(GameService gameService, Validator validator) {
        this.gameService = gameService;
        this.validator = validator;
    }

    /**
     * Creates a new game if user doesn't have any ongoing or created games.
     * @param auth - currently logged-in user
     * @param createGameForm - the form containing the game creation parameters
     * @return - a response entity containing the map representation of the created game, or a BAD_REQUEST status if
     * the game cannot be created
     */
    @PostMapping("/create")
    public ResponseEntity<Map> createGame(Authentication auth, @RequestBody CreateGameForm createGameForm) {
        Integer targetScore = createGameForm.getTargetScore();
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You have an unfinished game!"));
        }
        //Checks if user provided a valid game mode (101, 301 or 501)
        if (!validator.validateTargetScore(targetScore)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong target score!"));
        }
        return ResponseEntity.ok(gameService.addNewGame(auth.getName(), targetScore));
    }

    /**
     * List all the ongoing or created games.
     * @return - a response entity containing the list of games, or a NOT_FOUND status if there are no active games
     */
    @GetMapping("/list")
    public ResponseEntity<List<Game>> getCurrentGames() {
        List<Game> listOfGames = gameService.getAllGames();
        if (listOfGames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOfGames);
        }
        return ResponseEntity.ok(listOfGames);
    }

    /**
     * Lets a user join to a created game.
     * @param auth - currently logged-in user
     * @param gameID - unique identifier of the requested game
     * @return - a response entity containing the map representation of the joined game, or different error
     * messages containing why player couldn't join the game
     */
    @GetMapping("/join/{gameID}")
    public ResponseEntity<Map> joinGame(Authentication auth, @PathVariable("gameID") String gameID) {

        //Checks if id is a number
        Long id = validator.validateLongInput(gameID);
        if (id == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
        }

        //Checks if game exists
        if (!gameService.isGame(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
        }

        //Checks if game is created by the same user that wants to join
        if (gameService.isCreatedByThisPlayer(auth.getName(), id)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You can't play alone!"));
        }

        //Checks if game is in "created" state
        if (!gameService.isAvailableToJoin(id)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You can't join the game!"));
        }

        //Checks if user has any ongoing games
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You have an unfinished game!"));
        }

        return ResponseEntity.ok(gameService.joinGame(auth.getName(), id));
    }

    /**
     * Shows the status of the user's ongoing or created game.
     * @param auth - currently logged-in user
     * @return - a response entity containing the map representation of the found game, or a NOT_FOUND status if
     * such a game does not exist
     */
    @GetMapping("/status")
    public ResponseEntity<Map> getGameStatus(Authentication auth) {
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.ok(gameService.getGameByName(auth.getName()));
        } else if (!gameService.findLastFinishedGame(auth.getName()).isEmpty()) {
            return ResponseEntity.ok(gameService.findLastFinishedGame(auth.getName()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
        }
    }

    /**
     * Handles the inputted thrown scores.
     * @param auth - currently logged-in user
     * @param dartsThrowForm - a form containing the throw parameters
     * @return - a response entity containing the map representation of the modified game, or different error
     * messages containing why provided throws could not be applied
     */
    @PostMapping("/throws")
    public ResponseEntity<Map> postThrows(Authentication auth, @RequestBody DartsThrowForm dartsThrowForm) {

        Map game = gameService.getGameByName(auth.getName());
        //Checks if player is in a game or not
        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "There are no games available!"));
        }

        Long gameId = (Long) game.get("gameId");

        //Creates ThrownDart objects from provided text if input data is valid
        ThrownDart[] thrownDarts = validator.validateThrows(gameService.getGame(gameId), auth.getName(), dartsThrowForm);

        //Checks if provided throws were correct or not
        if (thrownDarts.length == 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong throws!"));
        }

        //Checks if it is the user's turn or not
        if (!gameService.isTheirTurn(gameId, auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong turn!"));
        }

        //Modifies the state of the game
        return ResponseEntity.ok(gameService.applyThrows(gameId, auth.getName(), thrownDarts));
    }

    /**
     * Changes the state of a not finished game.
     * @param cancelGameForm - a form containing the cancel parameters
     * @return - a response entity containing the map representation of the modified game, or different error
     * messages containing why game could not be cancelled
     */
    @PutMapping(path = "/cancel")
    public ResponseEntity<Map> cancelGame(@RequestBody CancelGameForm cancelGameForm) {

        Long id = cancelGameForm.getGameId();

        if (id == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
        }

        if (!gameService.isGame(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
        }

        Game game = gameService.getGame(id);

        if (!validator.validateFinishedGameStatus(cancelGameForm.getStatus(), game)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong status!"));
        }

        if (game.getGameStatus().matches("\\S*\\swins!")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "The game is already over!"));
        }

        return ResponseEntity.ok(gameService.changeGameStatus(id, cancelGameForm.getStatus()));
    }


    /**
     * Reverts a game to a chosen previous state.
     * @param revertGameForm - a form containing parameters to revert a game
     * @return - a response entity containing the map representation of the modified game, or different error
     * messages containing why game could not be reverted
     */
    @PutMapping(path = "/revert")
    public ResponseEntity<Map> revertGame(@RequestBody RevertGameForm revertGameForm) {
        Long gameId = revertGameForm.getGameId();
        Integer move = revertGameForm.getMove();

        if (gameId == null || move == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
        }

        GameHistory gameHistory = gameService.getGameHistory(gameId);
        if (gameHistory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
        }
        if (gameService.getGame(gameId).getGameStatus().equals("created")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
        }
        if (gameHistory.getHistoryList().size() <= move) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Move not found!"));
        }
        if (move + 1 == gameHistory.getHistoryList().size()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "There is nothing to revert!"));
        }
        if (gameHistory.getHistoryList().get(gameHistory.getHistoryList().size() - 1).getGameStatus().matches("\\S*\\swins!")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "The game is over!"));
        }

        return ResponseEntity.ok(gameService.setGameBackToGameState(gameId, move));
    }

    /**
     * Not part of the project (yet) but it can delete any game from the table
     * @param gameID - unique identifier of the game that needs to be deleted
     * @return - Responds with success message, not found code, or error message if path variable is incorrect
     */
    @DeleteMapping(path = "/delete/{gameID}")
    public ResponseEntity<Map> deleteGame(@PathVariable("gameID") String gameID) {
        Long id = validator.validateLongInput(gameID);
        if (id == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
        }
        if (gameService.deleteGame(id)) {
            return ResponseEntity.ok(Collections.singletonMap("result", "Game with ID=" + id + " successfully deleted!"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
