/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package minesweeper;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import minesweeper.gui.StartSelectView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        StartSelectView startScreen = new StartSelectView();
        Scene scene = new Scene(startScreen.get());
        scene.getStylesheets().add("stylesheet.css");
        stage.setScene(scene);
        stage.show();
        // Add a general listener to the root view (StackPane), any changes to its childre will cause the stage to automagically resize itself to everything.
        startScreen.rootChildren().addListener((ListChangeListener.Change<? extends Node> change) -> {
                stage.sizeToScene();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
