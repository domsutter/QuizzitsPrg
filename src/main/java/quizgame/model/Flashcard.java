/*
 * ===============================================================
 * File: Flashcard.java
 * ===============================================================
 */

package quizgame.model;
import quizgame.util.StudentLogger;  

public class Flashcard {
    private String question;
    private String answer;

    public Flashcard() {
        StudentLogger.enter("names#Flashcard");  
 }


    public Flashcard(String question, String answer) {
        StudentLogger.enter("names#Flashcard");  
        this.question = question;
        this.answer = answer;
    }


    public String getQuestion() {
        StudentLogger.enter("names#getQuestion");  
 StudentLogger.step("About to return from method.");  
 return question; }

    public void setQuestion(String question) {
        StudentLogger.enter("names#setQuestion");  
 this.question = question; }


    public String getAnswer() {
        StudentLogger.enter("names#getAnswer");  
 StudentLogger.step("About to return from method.");  
 return answer; }

    public void setAnswer(String answer) {
        StudentLogger.enter("names#setAnswer");  
 this.answer = answer; }
}