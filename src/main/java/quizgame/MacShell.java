/*
 * ===============================================================
 * File: MacShell.java
 * ===============================================================
 */

package quizgame;
import quizgame.util.StudentLogger;  

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class MacShell {

    public static final double WIDTH = 960;
    public static final double HEIGHT = 600;

    private final ScreenRouter router;
    private final Stage stage;
    public MacShell(ScreenRouter router, Stage stage) {
        StudentLogger.enter("names#MacShell");  // we just entered this method
        this.router = router;
        this.stage = stage;
    }

    public Scene sceneWith(Node centerContent) {
        StudentLogger.enter("names#sceneWith");  
        BorderPane root = new BorderPane();  
        SidebarMain sidebarView = new SidebarMain(router);  
        Region sidebar = sidebarView.buildSidebar(stage);
        root.setLeft(sidebar);

        BorderPane centerWrap = new BorderPane(centerContent);  
        centerWrap.setPadding(new Insets(24));
        root.setCenter(centerWrap);

        Scene scene = new Scene(root, WIDTH, HEIGHT);  
        Styles.apply(scene);
        StudentLogger.step("About to return from method.");  
        return scene;  
    }
}