package ku;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.text.resources.iw.FormatData_iw_IL;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainWindow implements Initializable {
    public NumberAxis yAxis;
    public NumberAxis xAxis;
    public LineChart bc;
    public MenuBar menuBar;
    private God god;
    private static int epoch = 0;
    private TreeMap<Integer, XYChart.Series<Integer, Integer>> curves;
    private BlockingQueue<TreeMap<Integer, Integer>> renderingMsg;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        god = God.getInstance();
        renderingMsg = new LinkedBlockingQueue<>();
        curves = new TreeMap<>();
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Epoch");
        yAxis.setLabel("Label Percentage");
    }

    public void addNewMessage(ActionEvent mouseEvent) {
        god.insertNewMessage();
    }

    public void initCluster(ActionEvent mouseEvent) {
        god.initCluster();
        god.printGraph();
    }

    public void startCluster(ActionEvent mouseEvent) {
        god.start();
    }

    public void freezeCluster(ActionEvent mouseEvent) {
        god.stop();
    }


    public void loadConfig(ActionEvent mouseEvent) {
        String path = "../config.json";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Config File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ALL", "*.*")
        );
        File file1 = fileChooser.showOpenDialog(new Stage());
        if(file1 == null){
             return;
        }
        path = file1.toString();
        System.out.println(path);
        if(!path.endsWith(".json")){
            return;
        }
        Config cc = null;
        System.out.println(path);
        try {
            File file = new File(path);
            BufferedReader in = new BufferedReader(new FileReader(file));
            String s = in.readLine();
            if(s == null){
                in.close();
                file = new File("../config.json");
                in = new BufferedReader(new FileReader(file));
                s = in.readLine();
            }
            System.out.println("Read config : " + s);
            in.close();
            cc = new Gson().fromJson(s, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        god.setClusterConfig(cc);
    }

    public void saveConfig(ActionEvent mouseEvent) {
        String path = null;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Store Config file");
        File file1 = chooser.showDialog(new Stage());
        if(file1 == null){
            return;
        }
        System.out.println(file1);
        path = file1.toString()+"config.json";
        Config cc = new Config(god.getScale(), god.getUdpLossRate(), god.getSparse()) ;
        try {
            File file = new File(path);
            String s = new Gson().toJson(cc);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(s);
            out.close();
            System.out.println("Save Config : " + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConfigPane(ActionEvent mouseEvent) {
        ClusterConfigWindow ccw = new ClusterConfigWindow();
        ccw.display();
    }



    private void getNum(int which){
        GetNumWindow getNumWindow = new GetNumWindow();
        getNumWindow.display(which);
    }


    public void destroyHost(ActionEvent mouseEvent) {
        getNum(0);
    }

    public void reviveHost(ActionEvent mouseEvent) {
        getNum(1);
    }

    public void addNewHost(ActionEvent mouseEvent) {
        getNum(2);
    }

    void clearChart(){
        epoch = 0;
        bc.getData().remove(0, bc.getData().size());
        curves.clear();
    }


    private void updateChart(TreeMap<Integer, Integer> map){
        for(Map.Entry<Integer, Integer> entry: map.entrySet()){
            int version = entry.getKey();
            int num = entry.getValue();

            XYChart.Series<Integer, Integer> series = curves.get(version);
            if(series == null){
                series = new XYChart.Series<>();
                series.setName(version + " ");
                curves.put(version, series);
                bc.getData().add(series);
            }

            series.getData().add(new XYChart.Data<>(epoch, num));
        }
        epoch ++;
    }



    public void start() {
        Task<Void> listener = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()){
                    TreeMap<Integer, Integer> map = renderingMsg.take();
                    if(map.size() == 0) break; // the map is killing signal
                    Platform.runLater(() ->updateChart(map));
                }
                System.out.println("MainWindow over");
                return null;
            }
        };
        new Thread(listener).start();
    }


    public void addRenderingMsg(){
        renderingMsg.add(god.showMessagePercentage());
    }



    public void finish() {
        renderingMsg.add(new TreeMap<>());
    }

    public void restart(ActionEvent actionEvent) {
        god.initCluster();
        god.start();
    }
}

