package communicate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    private ClientReader clientReader;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("communicate.fxml"));
        Parent root = loader.load();
        controller=loader.getController();
        primaryStage.setTitle("Projekt");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        clientReader=new ClientReader(controller);
        Thread cr=new Thread(clientReader);
        cr.setDaemon(true);
        cr.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
