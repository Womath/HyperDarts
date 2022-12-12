package dartsgame.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final Map<String, String> status = new HashMap<>();
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Creates a new game if user doesn't have any ongoing or created games.
     * @param auth - User
     * @param createGameForm - Request's body
     * @return - Response as a Map (error result or state of the Game)
     */
    @PostMapping("/create")
    public ResponseEntity<Map> createGame(Authentication auth, @RequestBody CreateGameForm createGameForm) {
        //Checks if user has any ongoing or created games
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You have an unfinished game!"));
        }
        //Checks if user provided a valid game mode (101, 301 or 501)
        if (!gameService.validateTargetScore(createGameForm.getTargetScore())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong target score!"));
        }
        return ResponseEntity.ok(gameService.addNewGame(auth.getName(), createGameForm.getTargetScore()));
    }

    /**
     * List all the ongoing or created games
     * @return - Empty list or List of Game objects
     */
    @GetMapping("/list")
    public ResponseEntity<List<Game>> getCurrentGames() {
        List<Game> listOfGames = gameService.getAllGames();
        if (listOfGames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOfGames);
        }
        return ResponseEntity.ok(gameService.getAllGames());
    }

    /**
     * Lets a user join to a created game
     * @param auth - User
     * @param gameID - ID of the game user wants to join (from path)
     * @return - Response as a Map (error result or state of the Game)
     */
    @GetMapping("/join/{gameID}")
    public ResponseEntity<Map> joinGame(Authentication auth, @PathVariable("gameID") Long gameID) {
        //Checks if game exists
        if (!gameService.isGame(gameID)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
        }

        //Checks if game is created by the same user that wants to join
        if (gameService.isInThisGame(auth.getName(), gameID)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You can't play alone!"));
        }

        //Checks if game is in "created" state
        if (!gameService.isAvailableToJoin(gameID)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You can't join the game!"));
        }

        //Checks if user has any ongoing games
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You have an unfinished game!"));
        }

        return ResponseEntity.ok(gameService.joinGame(auth.getName(), gameID));
    }

    /**
     * Shows the status of the user's ongoing or created game
     * @param auth - User
     * @return - Response as a Map (error result or state of the Game)
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
     * Handles the inputted thrown scores
     * @param auth - User
     * @param dartsThrowForm - Object for request's body
     * @return - Response as a Map (error result or state of the Game)
     */
    @PostMapping("/throws")
    public ResponseEntity<Map> postThrows(Authentication auth, @RequestBody DartsThrowForm dartsThrowForm) {

        Map game = gameService.getGameByName(auth.getName());
        //Checks if player is in a game or not
        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "There are no games available!"));
        }

        Long gameId = (Long) game.get("gameId");

        //Creates ThrownDart objects from provided text
        ThrownDart[] thrownDarts = new ThrownDart[]{
                gameService.extractThrow(1, dartsThrowForm.getFirst()),
                gameService.extractThrow(2, dartsThrowForm.getSecond()),
                gameService.extractThrow(3, dartsThrowForm.getThird())};

        //Checks if it is a valid turn or not
        if (!gameService.validateThrows(gameId, auth.getName(), thrownDarts)) {
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
     * Not part of the project (yet) but it can delete any game from the table
     * @param gameID - ID of the game
     * @return - Responds with success message or not found code
     */
    @DeleteMapping(path = "/delete/{gameID}")
    public ResponseEntity<Map> deleteGame(@PathVariable("gameID") Long gameID) {
        if (gameService.deleteGame(gameID)) {
            return ResponseEntity.ok(Collections.singletonMap("result", "Game with ID=" + gameID + " successfully deleted!"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
