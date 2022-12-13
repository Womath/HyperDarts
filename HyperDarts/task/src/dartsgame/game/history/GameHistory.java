package dartsgame.game.history;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_history")
public class GameHistory {

    @Id
    @Column(name = "game_id")
    private  Long gameId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameState> historyList;

    public GameHistory() {
    }

    public GameHistory(Long gameId) {
        this.gameId = gameId;
        historyList = new ArrayList<>();
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<GameState> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<GameState> historyList) {
        this.historyList = historyList;
    }

    public void addNewGameState(Long gameId,
                                Integer move,
                                String playerOne,
                                String playerTwo,
                                String gameStatus,
                                Integer playerOneScore,
                                Integer playerTwoScore,
                                String turn) {

        historyList.add(
                new GameState(
                        gameId,
                        move,
                        playerOne,
                        playerTwo,
                        gameStatus,
                        playerOneScore,
                        playerTwoScore,
                        turn));
    }

    public void deleteGameStatesAfterRevert(Integer move) {
        int size = historyList.size();
        while (size > move + 1) {
            historyList.remove(historyList.size() - 1);
            size = historyList.size();
        }

    }

}
