package src;

import java.util.ArrayList;

public class Dealer extends Participants {
    private final int TOP_CARD_POS = 0;

    public Dealer() {
        super();
        currHandValue = 0;
        currHand = new ArrayList<Card>();
    }

    public int showDealerHand() {
        return currHand.get(TOP_CARD_POS).getValue();
    }
}
