package dartsgame.game.persistance;

import dartsgame.game.persistance.dao.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {

    @Query("SELECT gh FROM GameHistory gh WHERE gh.gameId = ?1")
    Optional<GameHistory> getGameHistoryById(Long id);
}
