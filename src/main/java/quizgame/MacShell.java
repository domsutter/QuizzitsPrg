/*
 * ===============================================================
 * File: MacShell.java
 * Chrome/layout wrapper that gives you a left sidebar, a system
 * menu bar, and a custom titlebar with mac-style traffic lights.
 * ===============================================================
 */
package quizgame;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class MacShell {

    // Used by App.java
    public static final double WIDTH  = 920;
    public static final double HEIGHT = 600;

    // Put traffic lights on the TOP RIGHT? (mac is false = left)
    private static final boolean TRAFFIC_RIGHT = true;

    // Prefs keys
    private static final String PREFS_NODE = "quizgame";
    private static final String KEY_DARK   = "darkMode";
    private static final String KEY_SIZE   = "baseFontSize";

    private final ScreenRouter router;
    private final Stage stage;

    public MacShell(ScreenRouter router, Stage stage) {
        this.router = router;
        this.stage = stage;
    }

    /** Compose a Scene with sidebar + your provided center content. */
    public Scene sceneWith(Region centerContent) {
        BorderPane root = new BorderPane();

        /* ===== System menu bar ===== */
        MenuBar menuBar = new MenuBar();
        try { menuBar.setUseSystemMenuBar(true); } catch (Throwable ignored) {}

        Menu app = new Menu("Settings");
        MenuItem about = new MenuItem("About Quizzits");
        MenuItem prefs = new MenuItem("Preferences…");
        MenuItem quit  = new MenuItem("Exit");
        quit.setOnAction(e -> Platform.exit());
        app.getItems().addAll(about, prefs, new SeparatorMenuItem(), quit);

        Menu file = new Menu("File");
        MenuItem newDeck = new MenuItem("New Deck");
        newDeck.setOnAction(e -> stage.setScene(router.createDeckScene(stage)));
        file.getItems().addAll(newDeck);

        menuBar.getMenus().addAll(app, file);

        /* ===== Custom title bar with traffic lights ===== */
        HBox titlebar = new HBox(8);
        titlebar.getStyleClass().add("titlebar");
        titlebar.setPadding(new Insets(8, 8, 8, 12));
        titlebar.setAlignment(Pos.CENTER_LEFT);

        HBox lights = buildTrafficLights(stage);
        Label windowTitle = new Label("Quizzits");
        windowTitle.getStyleClass().add("titlebar-title");

        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);

        if (TRAFFIC_RIGHT) {
            titlebar.getChildren().setAll(windowTitle, spring, lights);
        } else {
            titlebar.getChildren().setAll(lights, windowTitle, spring);
        }

        VBox topStack = new VBox(menuBar, titlebar);
        root.setTop(topStack);

        /* ===== Sidebar and center content ===== */
        var sidebar = new SidebarMain(router).buildSidebar(stage);
        root.setLeft(sidebar);

        BorderPane centerWrap = new BorderPane(centerContent);
        centerWrap.setPadding(new Insets(0));
        root.setCenter(centerWrap);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        Styles.apply(scene);

        // Apply saved preferences on scene creation
        applySavedPreferences(scene);

        // Wire menu items
        about.setOnAction(e -> showAboutSheet());
        prefs.setOnAction(e -> showPreferencesSheet(scene));

        return scene;
    }

    /* =========================== Preferences =========================== */

    private void applySavedPreferences(Scene scene) {
        Preferences p = Preferences.userRoot().node(PREFS_NODE);
        boolean dark = p.getBoolean(KEY_DARK, false);
        double size  = p.getDouble(KEY_SIZE, 15.0);

        // Font size live
        scene.getRoot().setStyle(String.format("-fx-font-size: %.0fpx;", size));

        // Dark stylesheet toggle
        toggleDarkStylesheet(scene, dark);
    }

    private void showPreferencesSheet(Scene scene) {
        Preferences p = Preferences.userRoot().node(PREFS_NODE);
        boolean dark = p.getBoolean(KEY_DARK, false);
        double size  = p.getDouble(KEY_SIZE, 15.0);

        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Preferences");
        dlg.initOwner(stage);
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.getDialogPane().getStyleClass().add("sheet");
        if (dlg.getDialogPane().getScene() != null) Styles.apply(dlg.getDialogPane().getScene());

        CheckBox darkToggle = new CheckBox("Dark mode");
        darkToggle.setSelected(dark);

        Label sizeLbl = new Label("Display Scale");
        sizeLbl.getStyleClass().add("field-label");

        Slider sizeSlider = new Slider(13, 20, size);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setBlockIncrement(1);

        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(10);
        gp.setPadding(new Insets(12));
       // gp.addRow(0, darkToggle);
       // gp.addRow(1, sizeLbl, sizeSlider);

        Label scaleValue = new Label(String.format("%.0f pt", sizeSlider.getValue()));
        scaleValue.getStyleClass().add("field-label");

        sizeSlider.valueProperty().addListener((obs, oldV, newV) ->
            scaleValue.setText(String.format("%.0f pt", newV.doubleValue()))
        );

        gp.addRow(0, darkToggle);
        gp.addRow(1, sizeLbl, sizeSlider, scaleValue);
        GridPane.setMargin(scaleValue, new Insets(0, 0, 0, 8));

       
        // Live updates + persist
        darkToggle.selectedProperty().addListener((obs, was, is) -> {
            p.putBoolean(KEY_DARK, is);
            toggleDarkStylesheet(scene, is);
        });

        sizeSlider.valueProperty().addListener((obs, ov, nv) -> {
            double v = Math.round(nv.doubleValue());
            p.putDouble(KEY_SIZE, v);
            scene.getRoot().setStyle(String.format("-fx-font-size: %.0fpx;", v));
        });

        dlg.getDialogPane().setContent(gp);
        dlg.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonData.OK_DONE));
        dlg.showAndWait();
    }

    private void toggleDarkStylesheet(Scene scene, boolean enable) {
        var darkUrl = Styles.class.getResource("dark.css");
        if (darkUrl == null) return; // dark.css is optional
        String css = darkUrl.toExternalForm();
        if (enable && !scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        } else if (!enable && scene.getStylesheets().contains(css)) {
            scene.getStylesheets().remove(css);
        }
    }

    private void showAboutSheet() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("About Quizzits");
        dlg.initOwner(stage);
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.getDialogPane().getStyleClass().add("sheet");
        if (dlg.getDialogPane().getScene() != null) Styles.apply(dlg.getDialogPane().getScene());

        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        box.setAlignment(Pos.TOP_LEFT);

        Label name = new Label("Quizzits");
        name.getStyleClass().add("headline");
        Label blurb = new Label("Group porject for CIS-1512, 2025 Fall Semester.");

        box.getChildren().addAll(name, blurb);
        dlg.getDialogPane().setContent(box);
        dlg.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonData.OK_DONE));
        dlg.showAndWait();
    }

    /* ========================= Traffic Lights ========================= */

    private HBox buildTrafficLights(Stage stage) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);

        StackPane close = trafficLight("#ff5f56");   // red
        StackPane min   = trafficLight("#ffbd2e");   // yellow
        StackPane zoom  = trafficLight("#27c93f");   // green

        attachCloseGlyph(close);
        attachMinGlyph(min);
        attachPlusGlyph(zoom);

        close.setOnMouseClicked(e -> stage.close());
        min.setOnMouseClicked(e -> stage.setIconified(true));
        zoom.setOnMouseClicked(e -> stage.setMaximized(!stage.isMaximized()));

        box.getChildren().addAll(close, min, zoom);
        return box;
    }

    private StackPane trafficLight(String hex) {
        Circle c = new Circle(6, Color.web(hex));
        c.setStroke(Color.rgb(0,0,0,0.25));
        c.setStrokeWidth(0.5);

        StackPane p = new StackPane(c);
        p.getStyleClass().add("traffic-light");
        p.setPrefSize(14, 14);
        p.setMinSize(14, 14);
        p.setMaxSize(14, 14);
        return p;
    }

    private void attachCloseGlyph(StackPane p) {
        Line a = new Line(0,0,0,0);
        Line b = new Line(0,0,0,0);
        for (Line ln : new Line[]{a,b}) {
            ln.setStroke(Color.rgb(0,0,0,0.6));
            ln.setStrokeWidth(1.2);
            ln.setVisible(false);
        }
        p.getChildren().addAll(a,b);
        p.hoverProperty().addListener((obs, oldV, hover) -> { a.setVisible(hover); b.setVisible(hover); });
        p.widthProperty().addListener((o, ov, nv) -> layoutXGlyph(p, a, b));
        p.heightProperty().addListener((o, ov, nv) -> layoutXGlyph(p, a, b));
    }
    private void layoutXGlyph(StackPane p, Line a, Line b) {
        double w = p.getWidth(), h = p.getHeight(), pad = 3.5;
        a.setStartX(pad); a.setStartY(pad); a.setEndX(w - pad); a.setEndY(h - pad);
        b.setStartX(pad); b.setStartY(h - pad); b.setEndX(w - pad); b.setEndY(pad);
    }

    private void attachMinGlyph(StackPane p) {
        Line m = new Line(0,0,0,0);
        m.setStroke(Color.rgb(0,0,0,0.6));
        m.setStrokeWidth(1.4);
        m.setVisible(false);
        p.getChildren().add(m);
        p.hoverProperty().addListener((obs, oldV, hover) -> m.setVisible(hover));
        p.widthProperty().addListener((o, ov, nv) -> layoutMinusGlyph(p, m));
        p.heightProperty().addListener((o, ov, nv) -> layoutMinusGlyph(p, m));
    }
    private void layoutMinusGlyph(StackPane p, Line m) {
        double w = p.getWidth(), h = p.getHeight(), pad = 3.3;
        m.setStartX(pad); m.setEndX(w - pad); m.setStartY(h/2.0); m.setEndY(h/2.0);
    }

    private void attachPlusGlyph(StackPane p) {
        Line h = new Line(0,0,0,0);
        Line v = new Line(0,0,0,0);
        for (Line ln : new Line[]{h,v}) {
            ln.setStroke(Color.rgb(0,0,0,0.6));
            ln.setStrokeWidth(1.2);
            ln.setVisible(false);
        }
        p.getChildren().addAll(h, v);
        p.hoverProperty().addListener((obs, oldV, hover) -> { h.setVisible(hover); v.setVisible(hover); });
        p.widthProperty().addListener((o, ov, nv) -> layoutPlusGlyph(p, h, v));
        p.heightProperty().addListener((o, ov, nv) -> layoutPlusGlyph(p, h, v));
    }
    private void layoutPlusGlyph(StackPane p, Line h, Line v) {
        double w = p.getWidth(), H = p.getHeight(), pad = 3.6;
        h.setStartX(pad); h.setEndX(w - pad); h.setStartY(H/2.0); h.setEndY(H/2.0);
        v.setStartX(w/2.0); v.setEndX(w/2.0); v.setStartY(pad); v.setEndY(H - pad);
    }
}
