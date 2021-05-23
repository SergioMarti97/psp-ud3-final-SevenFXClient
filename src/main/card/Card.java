package main.card;

import java.io.Serializable;

public class Card implements Serializable {

    private Club club;

    private Symbol symbol;

    public Card(Club club, Symbol symbol) {
        this.club = club;
        this.symbol = symbol;
    }

    public Club getClub() {
        return club;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol.getNumber() + " of " + club.getName();
    }

}
