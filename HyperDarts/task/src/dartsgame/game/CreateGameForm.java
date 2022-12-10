package dartsgame.game;

public class CreateGameForm {
    private Integer targetScore;

    public CreateGameForm() {
    }

    public CreateGameForm(Integer targetScore) {
        this.targetScore = targetScore;
    }

    public Integer getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(Integer targetScore) {
        this.targetScore = targetScore;
    }
}
