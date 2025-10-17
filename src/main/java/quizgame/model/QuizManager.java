/*
 * ===============================================================
 * File: QuizManager.java
 * ===============================================================
 */

package quizgame.model;
import quizgame.util.StudentLogger;  

import java.util.Collections;
import java.util.List;

public class QuizManager {
    private final Deck deckObject;
    private int index = 0;
    private int score = 0;

    public QuizManager(Deck deckObject) {
        StudentLogger.enter("names#QuizManager");  
        this.deckObject = deckObject;
    }

    public void shuffle() {
        StudentLogger.enter("names#shuffle");  
        Collections.shuffle(deckObject.getCards());
    }


    public void resetAndShuffle() {
        StudentLogger.enter("names#resetAndShuffle");  
        index = 0;
        score = 0;
        shuffle();
    }


    public boolean submitAnswer(String userAnswer) {
        StudentLogger.enter("names#submitAnswer");  
        if (index >= deckObject.getCards().size()) return false;  
        Flashcard current = deckObject.getCards().get(index);
        boolean correct = current.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
        if (correct) score++;  
        StudentLogger.step("About to return from method.");  
        return correct;  
    }

/**
 * STUDENT NOTE: What does `next()` do?
 * - Summarize the purpose inputFile your own words.
 * - Identify important inputs/outputs.
 * Study Tip: Trace a tiny example through this method.
 */
    public void next() {
        StudentLogger.enter("names#next");  
        index++;
    }

    public boolean hasNext() {
        StudentLogger.enter("names#hasNext");  
        StudentLogger.step("About to return from method.");  

        return index + 1 < deckObject.getCards().size();  
    }

    public int getIndex() {
        StudentLogger.enter("names#getIndex");  
 StudentLogger.step("About to return from method.");  
 return index; }

    public int getScore() {
        StudentLogger.enter("names#getScore");  
 StudentLogger.step("About to return from method.");  
 return score; }

    public int size() {
        StudentLogger.enter("names#size");  
 StudentLogger.step("About to return from method.");  
 return deckObject.getCards().size(); }


    public String currentQuestion() {
        StudentLogger.enter("names#currentQuestion");  
        StudentLogger.step("About to return from method.");  

        return deckObject.getCards().get(index).getQuestion();  
    }


    public String currentAnswer() {
        StudentLogger.enter("names#currentAnswer");  
        StudentLogger.step("About to return from method.");  

        return deckObject.getCards().get(index).getAnswer();  
    }
}