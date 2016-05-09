package src;

import java.util.Collections;
import java.util.LinkedList;

import src.CardTypes.Rank;
import src.CardTypes.Suit;

public class Deck {
    private static LinkedList<Card> fullDeck;
    private final int DECK_HEAD = 0;
    private LinkedList<Card> cards;
    
    
    public Deck() {
        if (fullDeck == null)
            createDeck();
        
        cards = new LinkedList<Card>();
        cards.addAll(fullDeck);
    }
    
    /**
     * Creates numDecks number of decks to use
     * @param numDecks
     */
    public Deck(int numDecks) {
        if (fullDeck == null)
            createDeck();
        
        cards = new LinkedList<Card>();
        while (numDecks > 0) {
            cards.addAll(fullDeck);
            numDecks--;
        }
    }
    
    static private void createDeck() {
        fullDeck = new LinkedList<Card>();
        for (Rank r : Rank.values()) {
            for (Suit s : Suit.values()) {
                Card newCard = new Card(r, s);
                fullDeck.add(newCard);
            }
        }
    }
    
    
    public void shuffleDeck() {
        Collections.shuffle(cards);
    }
    
    public Card dealCard() {
        //if deck ran out of cards, add discarded cards back into deck, reshuffle
        if (cards.isEmpty()) {
            addCardsToDeck();
            shuffleDeck();
        }
        
        Card c = cards.remove(DECK_HEAD);
        return c;
    }
    
    /**
     * Adds a new deck of 52 cards to current deck
     */
    private void addCardsToDeck() {
        cards.addAll(fullDeck);
    }
}
