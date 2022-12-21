package dartsgame.game.controller;

import dartsgame.game.service.GameService;
import dartsgame.game.persistance.dao.GameHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/history")
public class GameHistoryController {

    private final GameService gameService;

    @Autowired
    public GameHistoryController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(path = "/{gameID}")
    public ResponseEntity getGameHistory (@PathVariable("gameID") String id) {
        try {
            long gameID = Long.parseLong(id);
            if (gameID < 0) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
            }

            GameHistory gameHistory = gameService.getGameHistory(gameID);
            if (gameHistory == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("result", "Game not found!"));
            }

            return ResponseEntity.ok(gameHistory.getHistoryList());

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("result", "Wrong request!"));
        }

    }
}
