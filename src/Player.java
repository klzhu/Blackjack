package src;

import java.util.ArrayList;

public class Player extends Participants {
    private final int STARTING_MONEY = 1000;
    private static long playerIdCounter = 0;

    private long playerId;
    private boolean isWinner;
    private boolean busted;
    private boolean tied;
    private boolean doubleDown;
    private boolean hasSplit;
    private boolean splitWon;
    private boolean splitBusted;
    private boolean splitTied;
    private int bet;
    private int splitBet;
    private int hitsLeft;
    private int splitHitsLeft;
    private int moneyLeft;
    private int splitHandValue;
    private ArrayList<Card> splitHand;
    private ArrayList<Integer> splitAcePositions;

    public Player() {
        super();
        doubleDown = false;
        tied = false;
        isWinner = false;
        busted = false;
        hasSplit = false;
        splitWon = false;
        splitBusted = false;
        splitTied = false;
        playerId = playerIdCounter++;
        moneyLeft = STARTING_MONEY;
        splitAcePositions = new ArrayList<Integer>();
    }

    public long getPlayerId() {
        return playerId;
    }

    public void makeBet(int betAmt) {
        bet += betAmt;
        moneyLeft -= betAmt;
    }

    public void makeSplitBet(int betAmt) {
        splitBet += betAmt;
        moneyLeft -= betAmt;
    }

    public boolean getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(boolean w) {
        isWinner = w;
    }

    public boolean getBusted() {
        return busted;
    }

    public void setBusted(boolean b) {
        busted = b;
    }

    public boolean getTied() {
        return tied;
    }

    public void setTied(boolean t) {
        tied = t;
    }

    public int getMoneyLeft() {
        return moneyLeft;
    }

    public void resetPlayerVars() {
        bet = 0;
        splitBet = 0;
        splitHandValue = 0;
        splitHitsLeft = 0;
        tied = false;
        busted = false;
        isWinner = false;
        doubleDown = false;
        hasSplit = false;
        splitHand = null;
        splitWon = false;
        splitTied = false;
        splitAcePositions = new ArrayList<Integer>();
    }

    public int getBet() {
        return bet;
    }

    public void addMoney(int amt) {
        moneyLeft += amt;
    }

    public boolean getDoubleDown() {
        return doubleDown;
    }

    public void doubleDown() {
        doubleDown = true;
        hitsLeft = 1;
        if (hasSplit)
            splitHitsLeft = 1;
    }

    public void decHitsLeft() {
        hitsLeft--;
    }

    public int getHitsLeft() {
        return hitsLeft;
    }

    public boolean cardsHaveSameVal() {
        if (currHand.size() < 2)
            return false;
        int cardVal = currHand.get(0).getValue();
        for (int i = 1; i < currHand.size(); i++) {
            if (currHand.get(1).getValue() != cardVal)
                return false;
        }
        return true;
    }

    public void splitPair() {
        hasSplit = true;
        splitHand = new ArrayList<Card>();

        Card c = currHand.remove(1);
        currHandValue -= c.getValue();

        makeSplitBet(bet);
        addSplitCard(c);
        
        //we need to now update our ace positions if we had any
        for (int i = 0; i < acePositions.size(); i++)
        {
            if (acePositions.get(i) == 1) {
                splitAcePositions.add(acePositions.remove(i));
            }
        }
    }

    public boolean getHasSplit() {
        return hasSplit;
    }

    public void addSplitCard(Card c) {
        splitHandValue += c.getValue();
        splitHand.add(c);
    }

    public ArrayList<Card> getSplitHand() {
        return splitHand;
    }

    public void splitBusted() {
        splitBusted = true;
    }

    public boolean getSplitBusted() {
        return splitBusted;
    }

    public int getSplitHandValue() {
        return splitHandValue;
    }

    public int getSplitHitsLeft() {
        return splitHitsLeft;
    }

    public void decSplitHitsLeft() {
        splitHitsLeft--;
    }

    public int getNextSplitAcePosition() {
        if (splitAcePositions.isEmpty())
            return -1;
        return splitAcePositions.remove(0);
    }

    public void addToSplitAcePositions(int acePos) {
        splitAcePositions.add(acePos);
    }

    public void recalcSplitHandValue() {
        splitHandValue = 0;
        for (int i = 0; i < splitHand.size(); i++) {
            splitHandValue += splitHand.get(i).getValue();
        }
    }

    public boolean getSplitWon() {
        return splitWon;
    }

    public void setSplitWon() {
        splitWon = true;
    }

    public void setSplitTied() {
        splitTied = true;
    }

    public boolean getSplitTied() {
        return splitTied;
    }

    public int getSplitBet() {
        return splitBet;
    }
}
