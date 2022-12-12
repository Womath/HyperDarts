package dartsgame.game;

/**
 * An object to handle each thrown dart separately.
 */
public class ThrownDart {
    Integer numberOfDart;
    Integer multiplicator;
    Integer score;

    public ThrownDart() {
    }

    public ThrownDart(Integer numberOfDart) {
        this.numberOfDart = numberOfDart;
    }

    public ThrownDart(Integer numberOfDart, Integer multiplicator, Integer score) {
        this.numberOfDart = numberOfDart;
        this.multiplicator = multiplicator;
        this.score = score;
    }

    public Integer getNumberOfDart() {
        return numberOfDart;
    }

    public Integer getMultiplicator() {
        return multiplicator;
    }

    public Integer getScore() {
        return score;
    }
}
