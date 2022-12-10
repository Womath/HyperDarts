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

    Map<String, String> status = new HashMap<>();
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map> createGame(Authentication auth, @RequestBody CreateGameForm createGameForm) {
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "You have an unfinished game!"));
        }
        if (!gameService.validateTargetScore(createGameForm.getTargetScore())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong target score!"));
        }
        return ResponseEntity.ok(gameService.addNewGame(auth.getName(), createGameForm.getTargetScore()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Game>> getCurrentGames() {
        List<Game> listOfGames = gameService.getAllGames();
        if (listOfGames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOfGames);
        }
        return ResponseEntity.ok(gameService.getAllGames());
    }

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

    @GetMapping("/status")
    public ResponseEntity<Map> getGameStatus(Authentication auth) {
        if (gameService.isInGame(auth.getName())) {
            return ResponseEntity.ok(gameService.getGameByName(auth.getName()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
        }
    }

    @PostMapping("/throws")
    public ResponseEntity<Map> postThrows(Authentication auth) {
        status.put("status", auth.getName());
        return ResponseEntity.ok(status);
    }

    @DeleteMapping(path = "/delete/{gameID}")
    public ResponseEntity<Map> deleteGame(@PathVariable("gameID") Long gameID) {
        if (gameService.deleteGame(gameID)) {
            return ResponseEntity.ok(Collections.singletonMap("result", "Game with ID=" + gameID + " successfully deleted!"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
