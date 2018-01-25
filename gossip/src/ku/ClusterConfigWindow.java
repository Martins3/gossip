package ku;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by martin on 17-10-10.
 */
public class ClusterConfigWindow {
    void display(){
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
        Label nameLabel = new Label("Scale:");
        nameLabel.setId("bold-label");
        GridPane.setConstraints(nameLabel, 0, 1);
        TextField nameInput = new TextField();
        GridPane.setConstraints(nameInput, 1, 1);

        Label ansLabel = new Label("Udp Loss Rate:");
        GridPane.setConstraints(ansLabel, 0, 2);
        TextField ansInput = new TextField();
        GridPane.setConstraints(ansInput, 1, 2);

        Label passLabel = new Label("Sparse:");
        GridPane.setConstraints(passLabel, 0, 3);
        TextField passInput = new TextField();
        GridPane.setConstraints(passInput, 1, 3);


        Button loginButton = new Button("Ok");
        GridPane.setConstraints(loginButton, 1, 4);

        loginButton.setOnAction(e ->{
            God god = God.getInstance();
            String scale_s = nameInput.getText();
            String udpLossRate_s = ansInput.getText();
            String sparse_s = passInput.getText();

            int scale = -1, udpLossRate = -1;
            double sparse = -1;
            try {
                if(scale_s != null && scale_s.length() > 0) scale = Integer.parseInt(scale_s);
                if(udpLossRate_s != null && udpLossRate_s.length() > 0) udpLossRate = Integer.parseInt(udpLossRate_s);
                if(sparse_s != null && sparse_s.length() > 0) sparse = Double.parseDouble(sparse_s);
            }catch (Exception exc){
                System.out.println("Error: Check the input before OK");
                return;
            }

            Config config = new Config(scale, udpLossRate, sparse);
            god.setClusterConfig(config);
            System.out.printf("scale %d\nudp loss rate %d\nsparse %f\n", scale, udpLossRate, sparse);
            window.close();
        });

        //Add everything to grid
        grid.getChildren().addAll(nameLabel, nameInput, passLabel, passInput,
                loginButton, ansInput, ansLabel, notification);
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("rsc/Viper.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }
}

