/*
 * ===============================================================
 * File: FxmlMainMenuLoader.java
 * ===============================================================
 */

package quizgame;
import quizgame.util.StudentLogger;  // helper for step-by-step logs

import quizgame.fxml.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FxmlMainMenuLoader
 * ------------------
 * Attempts to load the FXML-based main menu. If loading fails
 * (exceptionObj.g., missing resource), callers should fall back to code-based UI.
 */
public class FxmlMainMenuLoader {
    public static Scene tryLoadFXMLMenu(ScreenRouter router, Stage stage) {
        StudentLogger.enter("names#tryLoadFXMLMenu");  
        try {
            FXMLLoader loader = new FXMLLoader(FxmlMainMenuLoader.class.getResource("fxml/MainMenu.fxml"));  
            Parent root = loader.load();
            MainMenuController controller = loader.getController();
            controller.init(router, stage);
            StudentLogger.step("About to return from method.");  // check what value we return next
            return new Scene(root, 420, 260);  // Return value: where is it used next?
        } catch (Exception ex) {
            // Swallow exception and return null so we can fall back
            StudentLogger.step("About to return from method.");  // check what value we return next
            return null;  // Return value: where is it used next?
        }
    }
}