package dartsgame.game.persistance;

import dartsgame.game.persistance.dao.GameState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStateRepository extends JpaRepository<GameState, Integer> {
}
