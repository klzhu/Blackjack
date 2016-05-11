package src;

import java.util.ArrayList;

public class Participants {
    protected int currHandValue;
    protected ArrayList<Card> currHand;
    protected ArrayList<Integer> acePositions;
    protected boolean hasBlackJack;

    public Participants() {
        hasBlackJack = false;
        acePositions = new ArrayList<Integer>();
        currHandValue = 0;
        currHand = new ArrayList<Card>();
    }

    public void addCard(Card c) {
        currHand.add(c);
        currHandValue += c.getValue();
    }

    public int getHandValue() {
        return currHandValue;
    }

    public ArrayList<Card> getCurrHand() {
        return currHand;
    }

    public void resetHand() {
        currHand = new ArrayList<Card>();
        currHandValue = 0;
        acePositions = new ArrayList<Integer>();
    }

    public int getNextAcePosition() {
        if (acePositions.isEmpty())
            return -1;
        return acePositions.remove(0);
    }

    public void addToAcePositions(int acePos) {
        acePositions.add(acePos);
    }

    public void recalcHandValue() {
        currHandValue = 0;
        for (int i = 0; i < currHand.size(); i++) {
            currHandValue += currHand.get(i).getValue();
        }
    }

    public boolean getBlackJack() {
        return hasBlackJack;
    }

    public void setBlackJack(boolean blackjack) {
        hasBlackJack = blackjack;
    }

    public void resetBlackJack() {
        hasBlackJack = false;
    }
}
