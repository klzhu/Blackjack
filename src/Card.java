package src;

import src.CardTypes.Rank;

public class Card {
    private CardTypes.Rank rank;
    private CardTypes.Suit suit;
    private int value;

    public Card(CardTypes.Rank r, CardTypes.Suit s) {
        rank = r;
        suit = s;
        
        value = setValue();
    }

    public boolean isAce() {
        return rank == Rank.Ace;
    }

    public int swapAceValue() {
        if (isAce())
            value = 1;
        return -1;
    }
    
    public int getValue() {
        return value;
    }

    private int setValue() {
        switch (rank) {
        case Ace:
            return 11;
        case Two:
            return 2;
        case Three:
            return 3;
        case Four:
            return 4;
        case Five:
            return 5;
        case Six:
            return 6;
        case Seven:
            return 7;
        case Eight:
            return 8;
        case Nine:
            return 9;
        case Ten:
        case Jack:
        case Queen:
        case King:
            return 10;
        default:
            return -1;
        }
    }
    
    public String getCardName() {
        return rank + " of " + suit;
    }

}
