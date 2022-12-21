package dartsgame.game.persistance.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@JsonIgnoreProperties(value = "databaseId")
@Entity
@Table(name = "game_state")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;
    @Column(name = "game_id")
    private Long gameId;
    @Column(name = "move")
    private Integer move;
    @Column(name = "player_one")
    private String playerOne;
    @Column(name = "player_two")
    private String playerTwo;
    @Column(name = "game_status")
    private String gameStatus;
    @Column(name = "player_one_scores")
    private Integer playerOneScores;
    @Column(name = "player_two_scores")
    private Integer playerTwoScores;
    @Column(name = "turn")
    private String turn;

    public GameState(Long gameId,
                     Integer move,
                     String playerOne,
                     String playerTwo,
                     String gameStatus,
                     Integer playerOneScores,
                     Integer playerTwoScores,
                     String turn) {

        this.gameId = gameId;
        this.move = move;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameStatus = gameStatus;
        this.playerOneScores = playerOneScores;
        this.playerTwoScores = playerTwoScores;
        this.turn = turn;
    }
}
