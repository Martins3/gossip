package ku;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class GetNumWindow {

    /**
     *
     * @param which 0 : destroy 1: revive 2: add new Host
     */
    void display(int which){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.resizableProperty().setValue(Boolean.FALSE);
        window.setTitle("Reset");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        Label notification = new Label();
        GridPane.setConstraints(notification,0, 0);


        //Name Label - constrains use (child, column, row)
        Label nameLabel = new Label("Number:");
        nameLabel.setId("bold-label");
        GridPane.setConstraints(nameLabel, 0, 1);
        TextField nameInput = new TextField();
        GridPane.setConstraints(nameInput, 1, 1);

        Button loginButton = new Button("Ok");
        GridPane.setConstraints(loginButton, 1, 4);

        loginButton.setOnAction(e ->{
            String scale_s = nameInput.getText();
            if(scale_s == null) return;
            int num;
            try{
                num = Integer.parseInt(scale_s);
            }catch (Exception exc){
                System.out.println("Error: Integer Expected !");
                return;
            }
            God god = God.getInstance();
            switch (which){
                case 0:
                    god.randomDestroy(num);
                    break;
                case 1:
                    god.randomRevive(num);
                    break;
                case 2:
                    god.addNewHosts(num);
                    break;
                default:
                    System.out.println("There is a bug !");
                    return;
            }
            window.close();
        });

        //Add everything to grid
        grid.getChildren().addAll(nameLabel, nameInput,
                loginButton, notification);
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("rsc/Viper.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }
}
