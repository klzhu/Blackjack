package src;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class BlackJack {
    private final static int NUM_CARDS_PER_DECK = 52;
    private final static int NUM_CARDS_PER_STARTING_HAND = 2;
    private final static int BLACKJACK = 21;
    private final static double BLACKJACK_MULTIPLER = 3 / 2;
    private final static int DEFAULT_MULTIPLIER = 1;

    static Deck deck;
    static ArrayList<Player> players;
    static Dealer dealer;
    static BufferedReader r;
    static int numPlayers = 0;
    static int numPlayersBusted;
    static int numWinners;

    public static void main(String[] args) throws IOException {
        BufferedInputStream buf = new BufferedInputStream(System.in);
        r = new BufferedReader(new InputStreamReader(buf, StandardCharsets.UTF_8));
        Boolean validNumPlayers;
        String userAction = null;
        do {
            validNumPlayers = true;
            System.out.println("Please enter the number of players for this game as an integer");
            try {
                numPlayers = Integer.parseInt(r.readLine());
            } catch (Exception e) {
                validNumPlayers = false;
            }
        } while (validNumPlayers == false || numPlayers < 1 || numPlayers > Long.MAX_VALUE);

        dealer = new Dealer();

        // calculate the number of decks we need for the number of players so
        // everyone gets at least 2 cards to begin
        int numDecksNeeded = (int) Math
                .ceil(((double) (numPlayers + 1) * NUM_CARDS_PER_STARTING_HAND) / NUM_CARDS_PER_DECK);
        initializeTable();
        do {
            numPlayersBusted = 0;
            numWinners = 0;
            
            takeBets();
            playBlackJack(numDecksNeeded);
            payOut();
            cleanUp();

            if (numPlayers == 0)
                System.out.println("Sorry, looks like the game is over, there are no players left at the table");
            else {
                do {
                    System.out.println("Game over, there was " + numWinners
                            + " winning hands this game. Press q to quit, c to continue playing");
                    userAction = r.readLine().toLowerCase();
                } while (!userAction.equals("q") && !userAction.equals("c"));

            }
        } while (userAction.charAt(0) != 'q' && numPlayers != 0);

    }

    /**
     * plays a round of blackjack
     * 
     * @param numDecks
     * @throws IOException
     */
    public static void playBlackJack(int numDecks) throws IOException {
        // create a new deck per game, shuffle deck
        if (numDecks == 1)
            deck = new Deck();
        else
            deck = new Deck(numDecks);
        deck.shuffleDeck();

        // to begin, each player gets 2 cards
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            Card c1 = deck.dealCard();
            Card c2 = deck.dealCard();

            if (c1.isAce())
                currPlayer.addToAcePositions(currPlayer.getCurrHand().size() - 1);

            if (c2.isAce())
                currPlayer.addToAcePositions(currPlayer.getCurrHand().size() - 1);

            currPlayer.addCard(c1);
            currPlayer.addCard(c2);
        }

        Card c1 = deck.dealCard();
        Card c2 = deck.dealCard();

        if (c1.isAce())
            dealer.addToAcePositions(dealer.getCurrHand().size() - 1);

        if (c2.isAce())
            dealer.addToAcePositions(dealer.getCurrHand().size() - 1);

        dealer.addCard(c1);
        dealer.addCard(c2);

        checkForPlayerBlackJack();
        checkForDealerBlackJack();
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            if (currPlayer.getIsWinner() == false) {
                if (currPlayer.cardsHaveSameVal())
                    playerSplit(currPlayer);
                playerDoubleDown(currPlayer);
                playerMoves(currPlayer);
                if (currPlayer.getHasSplit())
                    playSplitMoves(currPlayer);
            }
        }
        if (numPlayersBusted == numPlayers)
            System.out.println("Looks like every player busted, game over");
        else
            dealerMoves();
    }

    /**
     * allows player p to split their hand
     * 
     * @param p
     * @throws IOException
     */
    private static void playerSplit(Player p) throws IOException {
        if (p.getMoneyLeft() < p.getBet()) {
            System.out.println("Sorry, you don't have enough money to split");
        } else {
            System.out.println("Dealer is showing card with value " + dealer.showDealerHand());
            String userInput;
            do {
                System.out.println("Player " + p.getPlayerId() + " may split. Enter 'Y' to do so, 'N' to continue.");
                userInput = r.readLine().toLowerCase();
            } while (!userInput.equals("y") && !userInput.equals("n"));

            if (userInput.charAt(0) == 'y') {
                p.splitPair();
            }
        }
    }

    /**
     * gives user chance to double down
     * 
     * @param p
     * @throws IOException
     */
    private static void playerDoubleDown(Player p) throws IOException {
        if (p.getMoneyLeft() == 0) {
            System.out.println("Player " + p.getPlayerId() + " has no money left and can't double down.");
            return;
        }

        System.out.println("Dealer is showing card with value " + dealer.showDealerHand());
        String userInput;
        do {
            System.out.println("Player " + p.getPlayerId()
                    + " may double down on first two cards. Enter 'Y' to do so, 'N' to continue."
                    + " If you have split, you will be doubling down on both hands.");
            userInput = r.readLine().toLowerCase();
        } while (!userInput.equals("y") && !userInput.equals("n"));

        if (userInput.charAt(0) == 'y') {
            p.doubleDown();
            boolean hasSplit = p.getHasSplit();
            boolean validBet;
            int doubleDownBet = -1;
            do {
                validBet = true;
                System.out.println("You have $" + p.getMoneyLeft() + " left, "
                        + "please enter the additional amount you'd like to bet up to 100% of your original bet. Please keep in mind if you've split,"
                        + "you need to have enough money to double down on your original and split hands.");
                try {
                    doubleDownBet = Integer.parseInt(r.readLine());
                } catch (Exception e) {
                    validBet = false;
                }
            } while (validBet == false || doubleDownBet < 0 || doubleDownBet > p.getBet()
                    || p.getMoneyLeft() < doubleDownBet || (hasSplit && p.getMoneyLeft() < 2 * doubleDownBet));
            p.makeBet(doubleDownBet);
            if (hasSplit)
                p.makeSplitBet(doubleDownBet);
        }
    }

    /**
     * Performs moves for dealer. Returns true if dealer lost, false otherwise
     */
    private static void dealerMoves() {
        boolean dealerLost = false;
        printDealerHand();
        while (dealer.getHandValue() < 17) {
            Card c = deck.dealCard();
            dealer.addCard(c);
            // if dealer's card is an ace and 11 puts dealer's hand over 21,
            // swap ace val
            if (c.isAce())
                dealer.addToAcePositions(dealer.getCurrHand().size() - 1);

            System.out.println("Dealer's new card is " + c.getCardName());
            if (dealer.getHandValue() > BLACKJACK) {
                if (!swapAceValue(dealer)) {
                    System.out.println("Dealer lost by going over 21.");
                    dealerLost = true;
                    break;
                }
            }
        }

        setWinners(dealerLost);
    }

    /**
     * If dealerLost is true, all players who didn't bust is a winner. Else,
     * everyone who had a higher hand than dealer and didn't bust is winner.
     * 
     * @param dealerLost
     */
    private static void setWinners(boolean dealerLost) {
        // if dealer lost, everyone who didn't bust is a winner
        if (dealerLost) {
            for (int i = 0; i < players.size(); i++) {
                Player currPlayer = players.get(i);
                if (currPlayer.getBusted() == false) {
                    currPlayer.setIsWinner(true);
                    numWinners++;
                }
                if (currPlayer.getHasSplit() && !currPlayer.getSplitBusted()) {
                    currPlayer.setSplitWon();
                    numWinners++;
                }
            }
        } else {
            int dealersHand = dealer.getHandValue();
            for (int i = 0; i < players.size(); i++) {
                Player currPlayer = players.get(i);
                if (!currPlayer.getBusted()) {
                    if (currPlayer.getHandValue() > dealersHand) {
                        currPlayer.setIsWinner(true);
                        numWinners++;
                    } else if (currPlayer.getHandValue() == dealersHand)
                        currPlayer.setTied(true);
                }

                if (currPlayer.getHasSplit() && !currPlayer.getSplitBusted()) {
                    if (currPlayer.getSplitHandValue() > dealersHand) {
                        currPlayer.setSplitWon();
                        numWinners++;
                    } else if (currPlayer.getSplitHandValue() == dealersHand)
                        currPlayer.setSplitTied();
                }
            }
        }
    }

    /**
     * performs move for current player
     * 
     * @param currPlayer
     * @throws IOException
     */
    private static void playerMoves(Player currPlayer) throws IOException {
        System.out.println("Dealer is showing card with value " + dealer.showDealerHand());
        String userInput;
        do {
            printPlayerHand(currPlayer);
            do {
                System.out.println("Enter 'H' to hit, 'S' to stay");
                userInput = r.readLine().toLowerCase();
            } while (!userInput.equals("h") && !userInput.equals("s"));

            if (currPlayer.getDoubleDown() && currPlayer.getHitsLeft() == 0 && userInput.charAt(0) != 's') {
                System.out.println("Sorry, you're out of hits and must stay");
                break;
            } else if (userInput.charAt(0) == 'h') {
                currPlayer.addCard(deck.dealCard());

                // if player doubled down previously, decrement number of hits
                // left
                if (currPlayer.getDoubleDown())
                    currPlayer.decHitsLeft();

                if (currPlayer.getHandValue() > BLACKJACK) {
                    if (!swapAceValue(currPlayer)) {
                        System.out.println("Player " + currPlayer.getPlayerId() + " went over 21 and loses.");
                        currPlayer.setBusted(true);
                        // if the player has not split, they have one hand and
                        // busted
                        if (!currPlayer.getHasSplit())
                            numPlayersBusted++;
                    }
                }
            }
        } while (currPlayer.getHandValue() <= BLACKJACK && userInput.charAt(0) != 's');
    }

    /**
     * Performs moves for player's split hand
     * 
     * @param currPlayer
     * @throws IOException
     */
    private static void playSplitMoves(Player currPlayer) throws IOException {
        System.out.println("These are the moves for your split hand");
        System.out.println("Dealer is showing card with value " + dealer.showDealerHand());
        String userInput;
        do {
            printPlayerSplitHand(currPlayer);
            do {
                System.out.println("Enter 'H' to hit, 'S' to stay");
                userInput = r.readLine().toLowerCase();
            } while (!userInput.equals("h") && !userInput.equals("s"));

            if (currPlayer.getDoubleDown() && currPlayer.getSplitHitsLeft() == 0 && userInput.charAt(0) != 's') {
                System.out.println("Sorry, you're out of hits and must stay");
                break;
            } else if (userInput.charAt(0) == 'h') {
                currPlayer.addSplitCard(deck.dealCard());

                // if player doubled down previously, decrement number of hits
                // left
                if (currPlayer.getDoubleDown())
                    currPlayer.decSplitHitsLeft();

                if (currPlayer.getSplitHandValue() > BLACKJACK) {
                    if (!swapSplitAceValue(currPlayer)) {
                        System.out.println(
                                "Player " + currPlayer.getPlayerId() + " went over 21 on split hand and loses.");
                        currPlayer.splitBusted();
                        // if player busted on original hand, then both hand has
                        // busted at this point
                        if (currPlayer.getBusted())
                            numPlayersBusted++;
                    }
                }
            }
        } while (currPlayer.getSplitHandValue() <= BLACKJACK && userInput.charAt(0) != 's');
    }

    /**
     * prints dealer's hand
     */
    private static void printDealerHand() {
        ArrayList<Card> currHand = dealer.getCurrHand();
        System.out.println("The current cards for the dealer is: ");
        for (int i = 0; i < currHand.size(); i++) {
            System.out.println(currHand.get(i).getCardName());
        }
        System.out.println("The current hand's value for the dealer is " + dealer.getHandValue());
    }

    /**
     * prints the player currplayer's hand
     * 
     * @param currPlayer
     */
    private static void printPlayerHand(Player currPlayer) {
        ArrayList<Card> currHand = currPlayer.getCurrHand();
        System.out.println("The current cards for player " + currPlayer.getPlayerId() + " is: ");
        for (int i = 0; i < currHand.size(); i++) {
            System.out.println(currHand.get(i).getCardName());
        }
        System.out.println(
                "The current hand's value for player " + currPlayer.getPlayerId() + " is " + currPlayer.getHandValue());
    }

    /**
     * prints the player currplayer's split hand
     * 
     * @param currPlayer
     */
    private static void printPlayerSplitHand(Player currPlayer) {
        ArrayList<Card> splitHand = currPlayer.getSplitHand();
        System.out.println("The current split hand cards for player " + currPlayer.getPlayerId() + " is: ");
        for (int i = 0; i < splitHand.size(); i++) {
            System.out.println(splitHand.get(i).getCardName());
        }
        System.out.println("The current split card hand's value for player " + currPlayer.getPlayerId() + " is "
                + currPlayer.getSplitHandValue());
    }

    /**
     * checks if player has 21 after first 2 cards. if player has above 21, must
     * have ace and we swap
     */
    private static void checkForPlayerBlackJack() {
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            if (currPlayer.getHandValue() == BLACKJACK) {
                currPlayer.setIsWinner(true);
                currPlayer.setBlackJack(true);
            }

            else if (currPlayer.getHandValue() > BLACKJACK) {
                // this can only happen right off the bat if the player has 2
                // aces, in which case, swap an aces value
                swapAceValue(currPlayer);
            }
        }
    }

    /**
     * checks if dealer has 21 after first 2 cards if dealer has above 21, must
     * have a ace and we simply swap one of the ace values
     */
    private static void checkForDealerBlackJack() {
        if (dealer.getHandValue() == BLACKJACK)
            dealer.setBlackJack(true);
        else if (dealer.getHandValue() > BLACKJACK) {
            // this can only happen right off the bat if the player has 2 aces,
            // in which case, swap an aces value
            swapAceValue(dealer);
        }
    }

    /**
     * for a given participant p, if that participant has a ace, swap the value
     * from 11 to 1. Return true if swap occured, false otherwise
     * 
     * @param p
     * @return
     */
    private static boolean swapAceValue(Participants p) {
        int acePos = p.getNextAcePosition();
        if (acePos != -1) {
            p.getCurrHand().get(acePos).swapAceValue();
            p.recalcHandValue();
            return true;
        }
        return false;
    }

    /**
     * for a given player, if that player's split hand has a ace, swap the value
     * from 11 to 1. Return true if swap occured, false otherwise
     * 
     * @param p
     * @return
     */
    private static boolean swapSplitAceValue(Player p) {
        int acePos = p.getNextSplitAcePosition();
        if (acePos != -1) {
            p.getSplitHand().get(acePos).swapAceValue();
            p.recalcSplitHandValue();
            return true;
        }
        return false;
    }

    /**
     * adds players to the players list
     */
    private static void initializeTable() {
        // add each player into game,
        players = new ArrayList<Player>();
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player());
        }
    }

    /**
     * takes each players bets at the start of the game
     */
    private static void takeBets() {
        for (int i = 0; i < numPlayers; i++) {
            Player currPlayer = players.get(i);
            boolean validBet;
            int betAmt = 0;
            do {
                validBet = true;
                System.out
                        .println("Player " + currPlayer.getPlayerId() + " currently has $" + currPlayer.getMoneyLeft());
                System.out.println("Please enter a integer amount to bet for player " + currPlayer.getPlayerId());
                try {
                    betAmt = Integer.parseInt(r.readLine());
                } catch (Exception e) {
                    validBet = false;
                }

                if (validBet && currPlayer.getMoneyLeft() < betAmt) {
                    System.out.println("You don't have enough money, please make a smaller bet");
                    validBet = false;
                }
            } while (validBet == false || betAmt < 0);
            currPlayer.makeBet(betAmt);
        }
    }

    /**
     * Pays the players after each round of blackjack if they won
     */
    private static void payOut() {
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            if (currPlayer.getBlackJack() && !dealer.getBlackJack()) {
                int moneyWon = (int) Math.round((double) (BLACKJACK_MULTIPLER * currPlayer.getBet()));
                moneyWon += currPlayer.getBet();
                currPlayer.addMoney(moneyWon);
            } else if (currPlayer.getIsWinner() && !currPlayer.getTied()) {
                int moneyWon = DEFAULT_MULTIPLIER * currPlayer.getBet();
                moneyWon += currPlayer.getBet();
                currPlayer.addMoney(moneyWon);
            } else if (currPlayer.getTied()) {
                currPlayer.addMoney(currPlayer.getBet());
            }

            // payout for split hands
            if (currPlayer.getHasSplit()) {
                if (currPlayer.getSplitWon() && !currPlayer.getSplitTied()) {
                    int splitMoneyWon = DEFAULT_MULTIPLIER * currPlayer.getSplitBet();
                    splitMoneyWon += currPlayer.getSplitBet();
                    currPlayer.addMoney(splitMoneyWon);
                } else if (currPlayer.getSplitTied()) {
                    currPlayer.addMoney(currPlayer.getSplitBet());
                }
            }
        }
    }

    /**
     * removes any players who has no money left
     * 
     * @param numPlayers
     */
    private static void cleanUp() {
        Iterator<Player> itr = players.iterator();
        while (itr.hasNext()) {
            Player currPlayer = itr.next();
            currPlayer.resetHand();
            currPlayer.resetPlayerVars();
            currPlayer.resetBlackJack();
            if (currPlayer.getMoneyLeft() == 0) {
                itr.remove();
                numPlayers--;
            }
        }

        dealer.resetHand();
        dealer.resetBlackJack();
    }
}
