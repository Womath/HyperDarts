package dartsgame.game.dto;

/**
 * Object of body for /throws API
 */
public class DartsThrowForm {

    private String first;
    private String second;
    private String third;

    public DartsThrowForm() {
    }

    public DartsThrowForm(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public DartsThrowForm(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }
}
