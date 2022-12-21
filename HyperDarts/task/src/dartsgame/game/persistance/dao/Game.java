package dartsgame.game.persistance.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "games")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "player_one")
    private String playerOne;

    @Column(name = "player_two")
    private String playerTwo;

    @Column(name = "game_status")
    private String gameStatus;

    @Column(name = "player_one_score")
    private Integer playerOneScores;

    @Column(name = "player_two_score")
    private Integer playerTwoScores;

    @Column(name = "turn")
    private String turn;

    public Game(String playerOne, Integer targetScore) {
        this.playerOne = playerOne;
        this.playerTwo = "";
        this.gameStatus = "created";
        this.playerOneScores = targetScore;
        this.playerTwoScores = targetScore;
        this.turn = playerOne;
    }

    public Map toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(this, Map.class);
    }
}
