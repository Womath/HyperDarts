package dartsgame.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface to work with games table through objects
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    //SELECT * FROM games WHERE player_one = ?
    @Query("SELECT g FROM Game g WHERE (g.gameStatus = 'created' OR g.gameStatus = 'started' OR g.gameStatus = 'playing') AND (g.playerOne = ?1 OR g.playerTwo = ?1)")
    Optional<Game> findGameByPlayerName(String name);

    @Query("SELECT g FROM Game g WHERE g.gameStatus LIKE '%wins%' AND (g.playerOne = ?1 OR g.playerTwo = ?1)")
    List<Game> findFinishedGamesByPlayerName(String name);
}
