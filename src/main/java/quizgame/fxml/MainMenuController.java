/*
 * ===============================================================
 * File: MainMenuController.java
 * ===============================================================
 */

package quizgame.fxml;
import quizgame.util.StudentLogger; 

import quizgame.ScreenRouter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    private ScreenRouter router;
    private Stage stage;

    public void init(ScreenRouter router, Stage stage) {
        StudentLogger.enter("names#init"); 
        this.router = router;
        this.stage = stage;
    }

    @FXML
    protected void onCreateDeck(ActionEvent exceptionObj) {
        StudentLogger.enter("names#onCreateDeck");  
        stage.setScene(router.createDeckScene(stage));
    }

    @FXML

    protected void onManageDecks(ActionEvent exceptionObj) {
        StudentLogger.enter("names#onManageDecks");  
        stage.setScene(router.manageDecksScene(stage));
    }

    @FXML

    protected void onStartQuiz(ActionEvent exceptionObj) {
        StudentLogger.enter("names#onStartQuiz");  
        stage.setScene(router.startQuizScene(stage));
    }

    @FXML

    protected void onExit(ActionEvent exceptionObj) {
        StudentLogger.enter("names#onExit"); 
        stage.close();
    }
}