package dartsgame.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    //SELECT * FROM games WHERE player_one = ?
    @Query("SELECT g FROM Game g WHERE g.playerOne = ?1")
    Optional<Game> findGameByPlayerOneName(String name);

    @Query("SELECT g FROM Game g WHERE g.playerTwo = ?1")
    Optional<Game> findGameByPlayerTwoName(String name);
}
