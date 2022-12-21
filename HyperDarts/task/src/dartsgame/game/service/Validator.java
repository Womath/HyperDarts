package dartsgame.game.service;

import dartsgame.game.persistance.dao.Game;
import dartsgame.game.dto.DartsThrowForm;
import dartsgame.game.dto.ThrownDart;
import org.springframework.stereotype.Component;

@Component
public class Validator {
    public Validator() {
    }

    /**
     * Validates an input that should be an integer number.
     * @param input - input string that needs to be checked
     * @return - an Integer if input is parsable, null otherwise
     */
    public Integer validateIntegerInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validates an input that should be a Long number.
     * @param input - input string that needs to be checked
     * @return - a Long if input is parsable, null otherwise
     */
    public Long validateLongInput(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validates if provided target score is a common darts game mode
     * @param targetScore - a number that needs to be checked
     * @return true if number is correct, false otherwise
     */
    public boolean validateTargetScore(Integer targetScore) {
        switch (targetScore) {
            case 101:
            case 301:
            case 501:
                return true;
            default:
                return false;
        }
    }

    /**
     * Validates if provided game status is a valid status of a finished game.
     * A correct finished status should look like this: playerName wins! or Nobody wins!
     * @param status - provided game status text
     * @param game - game that's status needs to be changed
     * @return - an array of the parts of the status
     */
    public boolean validateFinishedGameStatus(String status, Game game) {
        String[] statusArray = status.split(" ");

        if (statusArray.length != 2) {
            return false;
        }

        if (!statusArray[0].equals("Nobody")) {
            if (!statusArray[0].equals(game.getPlayerOne()) &&
                    !statusArray[0].equals(game.getPlayerTwo())) {
                return false;
            }
        }

        return statusArray[1].equals("wins!");
    }

    /**
     * Validates a set of thrown darts
     * @param game - the game which the darts were thrown
     * @param name - name of the player who threw the darts
     * @param dartsThrowForm - the form containing the thrown darts
     * @return - an array of thrown darts if provided values were correct, or an empty array otherwise
     */
    public ThrownDart[] validateThrows(Game game, String name, DartsThrowForm dartsThrowForm) {

        ThrownDart[] darts = new ThrownDart[]{
                extractThrow(1, dartsThrowForm.getFirst()),
                extractThrow(2, dartsThrowForm.getSecond()),
                extractThrow(3, dartsThrowForm.getThird())};

        for (ThrownDart dart : darts) {
            if (dart == null) {
                return new ThrownDart[0];
            }
        }

        for (ThrownDart dart : darts) {
            assert dart != null;
            if (dart.getNumberOfDart() == 1 && dart.getScore() == null) {
                return new ThrownDart[0];
            }
        }

        Integer playerScore;
        if (game.getPlayerOne().equals(name)) {
            playerScore = game.getPlayerOneScores();
        } else {
            playerScore = game.getPlayerTwoScores();
        }

        for (ThrownDart dart : darts) {
            if (playerScore > 1 && (dart.getScore() == null || dart.getMultiplicator() == null)) {
                return new ThrownDart[0];
            }
            if (playerScore <= 1 && (dart.getScore() != null || dart.getMultiplicator() != null)) {
                return new ThrownDart[0];
            }

            if (dart.getScore() != null && dart.getMultiplicator() != null) {
                playerScore -= dart.getMultiplicator() * dart.getScore();
            }
        }

        return darts;
    }

    /**
     * Extracts the values of the thrown dart from text
     * @param numberOfDart - numeric representation of the throw order
     * @param thrownDart - provided value of the throw in text
     * @return - a thrown dart if provided values are correct, null otherwise
     */
    private ThrownDart extractThrow(Integer numberOfDart, String thrownDart) {
        if (thrownDart.equals("none")) {
            return new ThrownDart(numberOfDart);
        }

        String[] values = thrownDart.split(":");
        Integer multiplicator = Integer.parseInt(values[0]);
        Integer score = Integer.parseInt(values[1]);
        if (validateThrowValue(multiplicator, score)) {
            return new ThrownDart(numberOfDart, multiplicator, score);
        } else {
            return null;
        }
    }

    /**
     * Checks that given values comply with the sectors of a darts board
     * @param multiplicator - represents a single, double or treble sector
     * @param score - represents the base value of the thrown sector
     * @return - true if all values are valid, false otherwise
     */
    private boolean validateThrowValue(Integer multiplicator, Integer score) {
        if (multiplicator < 1 || multiplicator > 3) {
            return false;
        }

        if (score >= 0 && score <= 20) {
            return true;
        } else return score == 25 && (multiplicator == 1 || multiplicator == 2);
    }

}
