package dartsgame.game;

/**
 * Object of body for /create API
 */
public class CreateGameForm {
    private String targetScore;

    public CreateGameForm() {
    }

    public CreateGameForm(String targetScore) {
        this.targetScore = targetScore;
    }

    public String getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(String targetScore) {
        this.targetScore = targetScore;
    }
}
