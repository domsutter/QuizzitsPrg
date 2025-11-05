/*
 * ===============================================================
 * File: Deck.java
 * ===============================================================
 */

package quizgame.model;
import quizgame.util.StudentLogger;  

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;

public class Deck {
    private String name;
    private List<Flashcard> cards = new ArrayList<>();  


    public Deck() {
        StudentLogger.enter("names#Deck");  
}

    public Deck(String name, List<Flashcard> cards) {
        StudentLogger.enter("names#Deck");  
        this.name = name;
        this.cards = cards;
    }

    public String getName() {
        StudentLogger.enter("names#getName");  
 StudentLogger.step("About to return from method.");  
 return name; }

    public void setName(String name) {
        StudentLogger.enter("names#setName");  
 this.name = name; }


    public List<Flashcard> getCards() {
        StudentLogger.enter("names#getCards");  
 StudentLogger.step("About to return from method.");  
 return cards; }

    public void setCards(List<Flashcard> cards) {
        StudentLogger.enter("names#setCards");  
 this.cards = cards; }

    //Shuffle cards
    public void shuffle() {
        StudentLogger.enter("names#shuffle");
        if (cards == null || cards.isEmpty()) {
            StudentLogger.step("No cards to shuffle.");
            return;
        }
        Collections.shuffle(cards);
        StudentLogger.step("Cards have been shuffled.");
    }

}