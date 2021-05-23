package main.card;

public enum Symbol {
    ONE(1, 1),
    TWO(2, 2),
    THREE(3, 3),
    FOUR(4, 4),
    FIVE(5, 5),
    SIX(6, 6),
    SEVEN(7, 7),
    JACK(10, .5f),
    HORSE(11, .5f),
    KING(12, .5f);

    private final int number;
    private final float points;

    Symbol(int number, float points) {
        this.number = number;
        this.points = points;
    }

    public int getNumber() {
        return number;
    }

    public float getPoints() {
        return points;
    }
}
