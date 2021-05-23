package main.card;

public enum Club {
    SWORD("Swords"),
    CUP("Cups"),
    COIN("Coins"),
    CLUB("Clubs");

    private final String name;

    Club(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
