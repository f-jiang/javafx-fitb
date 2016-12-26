package test;

import com.github.javafx_fitb.FillInTheBlanks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Feilan Jiang
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
		try {
			BorderPane root = new BorderPane();
			FillInTheBlanks fitb = new FillInTheBlanks();
			fitb.update("hello _", "_");
			root.setCenter(fitb);
			
			Scene scene = new Scene(root,400,400);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        launch(args);
    }
}