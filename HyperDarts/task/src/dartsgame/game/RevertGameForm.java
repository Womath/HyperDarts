package dartsgame.game;

public class RevertGameForm {
    private String gameId;
    private String move;

    public RevertGameForm() {
    }

    public RevertGameForm(String gameId, String move) {
        this.gameId = gameId;
        this.move = move;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
