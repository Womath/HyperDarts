package dartsgame.game.dto;

/**
 * This class represents a form for canceling a game.
 *
 * It has two properties:
 *  - gameId: the ID of the game to be canceled
 *  - status: the new status of the game (should be "CANCELED")
 */
public class CancelGameForm {
    private Long gameId;
    private String status;

    public CancelGameForm() {
    }

    public CancelGameForm(Long gameId, String status) {
        this.gameId = gameId;
        this.status = status;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
