package dartsgame.game.dto;

public class RevertGameForm {
    private Long gameId;
    private Integer move;

    public RevertGameForm() {
    }

    public RevertGameForm(Long gameId, Integer move) {
        this.gameId = gameId;
        this.move = move;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }
}
