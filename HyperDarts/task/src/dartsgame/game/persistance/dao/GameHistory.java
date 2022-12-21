package dartsgame.game.persistance.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameHistory {

    @Id
    @Column(name = "game_id")
    private  Long gameId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameState> historyList;

    public GameHistory(Long gameId) {
        this.gameId = gameId;
        historyList = new ArrayList<>();
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
