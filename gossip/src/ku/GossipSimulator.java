package ku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * cluster operation
 * 1. nodes(chart)
 * 2. add new message
 * 3. destroy some machine
 *
 * cluster config:
 * 1.
 *
 *
 */
public class GossipSimulator extends Application {

    private God god;
    private MainWindow mainWindow;
    @Override
    public void start(Stage window) throws Exception{
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("rsc/mainWindow.fxml"));
        Parent root = loginLoader.load();
        Scene scene = new Scene(root);
        mainWindow = loginLoader.getController();
        mainWindow.start();


        window.setTitle("Gossip Simulator");
        window.setScene(scene);
        window.show();



        god = God.getInstance();
        god.setWindow(mainWindow);
    }

    @Override
    public void stop(){
        god.finish();
        mainWindow.finish();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
