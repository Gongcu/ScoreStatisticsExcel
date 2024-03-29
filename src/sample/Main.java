package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    //성적 관리 프로그램
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
        primaryStage.setTitle("성적분석프로그램");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
