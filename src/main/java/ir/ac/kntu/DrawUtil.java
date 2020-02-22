package ir.ac.kntu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static javafx.scene.SceneAntialiasing.BALANCED;
import static javafx.scene.chart.XYChart.*;

/**
 * Method you might use :
 * <p>
 * addGraph(xs[]:double,ys[]:double,name:String)
 * add a graph for drawing
 * <p>
 * draw()
 * draws the given coordinates
 * <p>
 * addGraphAndDraw(xs[]:double,ys[]:double,name:String)
 * combination of addGraph and Draw
 */
public class DrawUtil extends Application {
    private static Stage primaryStage;
    private static Group root;
    private static Scene scene;
    private static Boolean started = Boolean.FALSE;
    private static LineChart<Number, Number> lineChart;
    private static Queue<Pair<Map<Double, Double>, String>> queue =
            new ConcurrentLinkedQueue<>();

    public static void addGraph(double[] xs, double[] ys,
                                String name) {
        TreeMap<Double, Double> map = new TreeMap<>();
        for (var i = 0; i < Math.min(xs.length, ys.length); i++) {
            map.put(xs[i], ys[i]);
        }
        queue.add(new Pair<>(map, name));
    }

    public static void addGraphAndDraw(double[] xs, double[] ys,
                                       String name) {
        addGraph(xs, ys, name);
        draw();
    }

    public static void draw() {
        new Thread(() -> {
            synchronized (DrawUtil.class) {
                if (!started) {
                    launch();
                }
                started = true;
            }
        }, "Drawer").start();
    }

    @Override
    public void init() {
        root = new Group();
        scene = new Scene(root, 1024, 768, false, BALANCED);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setResizable(false);
        stage.setTitle("Function Drawer");
        stage.setScene(scene);

        Axis<Number> xAxis = new NumberAxis();
        xAxis.setLabel("x");
        Axis<Number> yAxis = new NumberAxis();
        yAxis.setLabel("y = f(x)");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setPrefSize(1024, 768);

        VBox vbox = new VBox(lineChart);
        root.getChildren().addAll(vbox);
        stage.show();
        queue.forEach(this::drawChart);
    }

    private synchronized void drawChart(Pair<Map<Double, Double>, String> map) {
        Series<Number, Number> dataSeries = new Series<>();
        Set<Double> xs = map.getKey().keySet();
        dataSeries.setName(map.getValue());
        for (Double key : xs) {
            dataSeries.getData().add(new Data<>(key, map.getKey().get(key)));
        }
        lineChart.getData().add(dataSeries);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.setImplicitExit(true);
        Platform.runLater(() -> primaryStage.hide());
        started = false;
    }
}