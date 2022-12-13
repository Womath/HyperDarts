package dartsgame.game.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties(value = "databaseId")
@Entity
@Table(name = "game_state")
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

    public GameState() {
    }

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

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public Long getGameId() {
        return gameId;
    }

    public Integer getMove() {
        return move;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public Integer getPlayerOneScores() {
        return playerOneScores;
    }

    public Integer getPlayerTwoScores() {
        return playerTwoScores;
    }

    public String getTurn() {
        return turn;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setMove(Integer move) {
        this.move = move;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setPlayerOneScores(Integer playerOneScores) {
        this.playerOneScores = playerOneScores;
    }

    public void setPlayerTwoScores(Integer playerTwoScores) {
        this.playerTwoScores = playerTwoScores;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }
}
