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
    private int bet;
    private int splitBet;
    private int hitsLeft;
    private int splitHitsLeft;
    private int moneyLeft;
    private int splitHandValue;
    private ArrayList<Card> splitHand;

    public Player() {
        super();
        doubleDown = false;
        tied = false;
        isWinner = false;
        busted = false;
        hasSplit = false;
        splitWon = false;
        playerId = playerIdCounter++;
        moneyLeft = STARTING_MONEY;
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

    public void resetPlayerBools() {
        bet = 0;
        splitBet = 0;
        tied = false;
        busted = false;
        isWinner = false;
        doubleDown = false;
        hasSplit = false;
        splitHand = null;
        splitWon = false;
        splitHandValue = 0;
        splitHitsLeft = 0;
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
    }

    public boolean getHasSplit() {
        return hasSplit;
    }
    
    public void addSplitCard(Card c){
        splitHandValue += c.getValue();
        splitHand.add(c);
    }
}
