package dartsgame.game;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "games")
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

    public Game() {
    }

    public Game(String playerOne, Integer targetScore) {
        this.playerOne = playerOne;
        this.playerTwo = "";
        this.gameStatus = "created";
        this.playerOneScores = targetScore;
        this.playerTwoScores = targetScore;
        this.turn = playerOne;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Integer getPlayerOneScores() {
        return playerOneScores;
    }

    public void setPlayerOneScores(Integer playerOneScores) {
        this.playerOneScores = playerOneScores;
    }

    public Integer getPlayerTwoScores() {
        return playerTwoScores;
    }

    public void setPlayerTwoScores(Integer playerTwoScore) {
        this.playerTwoScores = playerTwoScore;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return Objects.equals(getGameId(), game.getGameId()) && Objects.equals(getPlayerOne(), game.getPlayerOne()) && Objects.equals(getPlayerTwo(), game.getPlayerTwo()) && Objects.equals(getGameStatus(), game.getGameStatus()) && Objects.equals(getPlayerOneScores(), game.getPlayerOneScores()) && Objects.equals(getPlayerTwoScores(), game.getPlayerTwoScores()) && Objects.equals(getTurn(), game.getTurn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGameId(), getPlayerOne(), getPlayerTwo(), getGameStatus(), getPlayerOneScores(), getPlayerTwoScores(), getTurn());
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID=" + gameId +
                ", playerOne='" + playerOne + '\'' +
                ", playerTwo='" + playerTwo + '\'' +
                ", gameStatus='" + gameStatus + '\'' +
                ", playerOneScores=" + playerOneScores +
                ", playerTwoScores=" + playerTwoScores +
                ", turn='" + turn + '\'' +
                '}';
    }
}
