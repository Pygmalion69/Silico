/*
 * Copyright (C) 2017 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package silico;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.NeedleBehavior;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleType;
import eu.hansolo.medusa.GaugeBuilder;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import silico.util.Util;

/**
 *
 * @author helfrich
 */
public class Silico extends Application implements SilicoGame.Callback {

    private static boolean consoleGame = false;
    private static SilicoGame silicoGame;

    private ResourceBundle rb;
    private Assets assets;
    Label courseCalcLabel;
    Label courseRealLabel;
    Label speedCalcLabel;
    Label speedRealLabel;
    Label xCalcLabel;
    Label xRealLabel;
    Label yCalcLabel;
    Label yRealLabel;
    Text cycleNumberText;
    Label baseXLabel;
    Label baseYLabel;
    private Gauge gauge;
    private Slider speedSlider;
    private Slider headingSlider;
    private Renderer renderer;
    private Label gameStateLabel;
    private HBox gameOverBox;
    private HBox controlBox;
    private GraphicsContext gc;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        silicoGame = new SilicoGame();
        silicoGame.init();

        consoleGame = (args.length > 0 && args[0].equals("-c"));

        silicoGame.run(consoleGame);
        if (!consoleGame) {
            launch(args);
        }

    }

    private void newGame() {
        silicoGame = new SilicoGame();
        silicoGame.init();
        silicoGame.run(false);
        renderer = new Renderer(this, silicoGame, gc, assets);
        renderer.render();
        gameOverBox.setVisible(false);
        controlBox.setVisible(true);
        headingSlider.setValue(0);
        speedSlider.setValue(0);
        silicoGame.setCallback(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        if (Util.isEmpty(Locale.getDefault().toString())) {
            Locale.setDefault(new Locale("nl"));
        }
        rb = ResourceBundle.getBundle("strings.strings");

        primaryStage.setTitle(rb.getString("Title"));

        assets = new Assets();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #a5a4b4");

        root.getStylesheets().add("css/silicoStyle.css");

        Pane leftPane = new Pane();
        leftPane.prefWidth(0);
        root.setLeft(leftPane);

        HBox topHBox = addTopHBox();
        root.setTop(topHBox);

        StackPane bottomPane = new StackPane();

        controlBox = addBottomHBox();

        controlBox.setVisible(true);
        controlBox.managedProperty().bind(controlBox.visibleProperty());

        gameOverBox = addGameOverBottomBox();

        gameOverBox.setVisible(false);
        gameOverBox.managedProperty().bind(gameOverBox.visibleProperty());

        bottomPane.getChildren().addAll(controlBox, gameOverBox);
        
        BorderPane.setMargin(bottomPane, new Insets(0, 0, 32, 0));

        root.setBottom(bottomPane);

        Canvas canvas = new Canvas(520, 400);
        gc = canvas.getGraphicsContext2D();

        VBox rightVBox = addRightBox();
        root.setRight(rightVBox);

        root.setCenter(canvas);

        primaryStage.setScene(new Scene(root, 920, 560));
        primaryStage.setResizable(false);
        primaryStage.show();

        newGame();
    }

    public HBox addTopHBox() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        //Button buttonCurrent = new Button("Current");
        //buttonCurrent.setPrefSize(100, 20);
        Text cycleLabel = new Text(rb.getString("Cycle"));
        cycleLabel.setFill(Color.valueOf("#4b4456"));
        cycleLabel.setFont(assets.getZorqueFontNormal());

        cycleNumberText = new Text("1");
        cycleNumberText.setFill(Color.valueOf("#4b4456"));
        cycleNumberText.setFont(assets.getZorqueFontNormal());
        //Button buttonProjected = new Button("Projected");
        //buttonProjected.setPrefSize(100, 20);
        hBox.getChildren().addAll(cycleLabel, cycleNumberText);

        return hBox;
    }

    private VBox addRightBox() {
        VBox vBox = new VBox();

        vBox.setPadding(new Insets(15, 12, 15, 12));
        vBox.setSpacing(10);
        // vBox.setStyle("-fx-background-color: #00ff00");

        Pane compass = addCompass();

        GridPane grid = new GridPane();

        grid.setPrefWidth(360);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        grid.setHgap(10);
        grid.setVgap(10);

        baseXLabel = new Label();
        GridPane.setHalignment(baseXLabel, HPos.RIGHT);

        Label baseLabel = new Label(rb.getString("BaseXY"));
        GridPane.setHalignment(baseLabel, HPos.CENTER);
        
        baseYLabel = new Label();
        GridPane.setHalignment(baseYLabel, HPos.LEFT);

        Label calculatedLabel = new Label(rb.getString("Calculated"));
        Separator separator = new Separator();
        GridPane.setHalignment(separator, HPos.CENTER);
        Label realLabel = new Label(rb.getString("Real"));

        grid.add(baseXLabel, 0, 0, 1, 1);
        grid.add(baseLabel, 1, 0, 1, 1);
        grid.add(baseYLabel, 2, 0, 1, 1);

        grid.add(calculatedLabel, 0, 1, 1, 1);
        grid.add(separator, 1, 1, 1, 1);
        grid.add(realLabel, 2, 1, 1, 1);

        courseCalcLabel = new Label();
        GridPane.setHalignment(courseCalcLabel, HPos.RIGHT);
        Label courseLabel = new Label(rb.getString("Course"));
        GridPane.setHalignment(courseLabel, HPos.CENTER);
        courseRealLabel = new Label();
        GridPane.setHalignment(courseRealLabel, HPos.LEFT);

        grid.add(courseCalcLabel, 0, 2, 1, 1);
        grid.add(courseLabel, 1, 2, 1, 1);
        grid.add(courseRealLabel, 2, 2, 1, 1);

        speedCalcLabel = new Label();
        GridPane.setHalignment(speedCalcLabel, HPos.RIGHT);
        Label speedLabel = new Label(rb.getString("Speed"));
        GridPane.setHalignment(speedLabel, HPos.CENTER);
        speedRealLabel = new Label();
        GridPane.setHalignment(speedRealLabel, HPos.LEFT);

        grid.add(speedCalcLabel, 0, 3, 1, 1);
        grid.add(speedLabel, 1, 3, 1, 1);
        grid.add(speedRealLabel, 2, 3, 1, 1);

        xCalcLabel = new Label();
        GridPane.setHalignment(xCalcLabel, HPos.RIGHT);
        Label xLabel = new Label(rb.getString("Xcoor"));
        GridPane.setHalignment(xLabel, HPos.CENTER);
        xRealLabel = new Label();
        GridPane.setHalignment(xRealLabel, HPos.LEFT);

        grid.add(xCalcLabel, 0, 4, 1, 1);
        grid.add(xLabel, 1, 4, 1, 1);
        grid.add(xRealLabel, 2, 4, 1, 1);

        yCalcLabel = new Label();
        GridPane.setHalignment(yCalcLabel, HPos.RIGHT);
        Label yLabel = new Label(rb.getString("Ycoor"));
        yLabel.setAlignment(Pos.CENTER);
        GridPane.setHalignment(yLabel, HPos.CENTER);
        yRealLabel = new Label();
        GridPane.setHalignment(yRealLabel, HPos.LEFT);

        grid.add(yCalcLabel, 0, 5, 1, 1);
        grid.add(yLabel, 1, 5, 1, 1);
        grid.add(yRealLabel, 2, 5, 1, 1);

        vBox.getChildren().addAll(compass, grid);
        return vBox;
    }

    private StackPane addCompass() {

        /*
        Medusa needs custom fonts for custom tick labels. For now, work around 
        this with a StackPane.
         */
        StackPane compassPane = new StackPane();

        compassPane.setPrefSize(160, 160);

        Label label90 = new Label("90");
        StackPane.setAlignment(label90, Pos.CENTER);
        StackPane.setMargin(label90, new Insets(0, 0, 0, 160));

        Label label180 = new Label("180");
        StackPane.setAlignment(label180, Pos.BOTTOM_CENTER);

        Label label270 = new Label("270");
        StackPane.setMargin(label270, new Insets(0, 160, 0, 0));
        StackPane.setAlignment(label270, Pos.CENTER);

        Label label360 = new Label("360");
        StackPane.setAlignment(label360, Pos.TOP_CENTER);

        gauge = GaugeBuilder.create()
                .prefSize(140, 140)
                .foregroundBaseColor(Color.valueOf("#4b4456"))
                .minValue(0)
                .maxValue(359)
                .startAngle(180)
                .angleRange(360)
                .autoScale(false)
                .customFontEnabled(true)
                .customFont(assets.getZorqueFontSmall())
                .customTickLabelsEnabled(true)
                //.customTickLabels("360", "", "90", "", "180", "", "270")
                .customTickLabels("", "", "", "", "", "", "")
                .customTickLabelFontSize(48)
                .minorTickMarksVisible(false)
                .mediumTickMarksVisible(false)
                .majorTickMarksVisible(false)
                .valueVisible(false)
                .needleType(NeedleType.FAT)
                .needleShape(NeedleShape.FLAT)
                .knobType(KnobType.FLAT)
                .needleColor(Color.valueOf("#7abc47"))
                .knobColor(Color.valueOf("#4b4456"))
                //.borderPaint(Gauge.DARK_COLOR)
                .animated(false)
                //.animationDuration(1500)
                .needleBehavior(NeedleBehavior.OPTIMIZED)
                .build();

        compassPane.getChildren().addAll(label90, label180, label270, label360, gauge);

        return compassPane;
    }

    private HBox addBottomHBox() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        // hBox.setStyle("-fx-background-color: #FFFFFF;");

        GridPane sliderGrid = new GridPane();
        // sliderGrid.setStyle("-fx-background-color: #FFFFFF;");

        sliderGrid.setPrefWidth(680);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(10);
        sliderGrid.getColumnConstraints().addAll(col1, col2, col3);

        sliderGrid.setHgap(10);
        sliderGrid.setVgap(10);

        Label headingLabel = new Label(rb.getString("Heading"));
        GridPane.setHalignment(headingLabel, HPos.RIGHT);
        GridPane.setValignment(headingLabel, VPos.CENTER);

        Label speedLabel = new Label(rb.getString("Speed"));
        GridPane.setHalignment(speedLabel, HPos.RIGHT);
        GridPane.setValignment(speedLabel, VPos.CENTER);

        headingSlider = new Slider(0, 360, 0);
        headingSlider.setMin(0);
        headingSlider.setMax(360);
        headingSlider.setValue(0);
        headingSlider.setShowTickLabels(false);
        headingSlider.setShowTickMarks(false);
        headingSlider.setMajorTickUnit(45);
        headingSlider.setMinorTickCount(10);
        headingSlider.setBlockIncrement(10);

        headingSlider.valueProperty()
                .addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    int val = Math.round(new_val.floatValue());
                    headingSlider.setValue(val);
                    if (val > 359) {
                        val = 0;
                    }
                    gauge.setValue(val);
                });

        GridPane.setHgrow(headingSlider, Priority.ALWAYS);

        speedSlider = new Slider(0, 30, 0);
        speedSlider.setMin(0);
        speedSlider.setMax(30);
        speedSlider.setValue(0);
        speedSlider.setShowTickLabels(false);
        speedSlider.setShowTickMarks(false);
        speedSlider.setMajorTickUnit(5);
        speedSlider.setMinorTickCount(10);
        speedSlider.setBlockIncrement(1);

        speedSlider.valueProperty()
                .addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    speedSlider.setValue(Math.round(new_val.floatValue()));
                });

        Label headingValueLabel = new Label("0");
        GridPane.setHalignment(headingValueLabel, HPos.LEFT);
        GridPane.setValignment(headingValueLabel, VPos.CENTER);

        Label speedValueLabel = new Label("0");
        GridPane.setHalignment(speedValueLabel, HPos.LEFT);
        GridPane.setValignment(speedValueLabel, VPos.CENTER);

        headingValueLabel.textProperty().bind(headingSlider.valueProperty().asString("%.0f"));
        speedValueLabel.textProperty().bind(speedSlider.valueProperty().asString("%.0f"));

        sliderGrid.add(headingLabel, 0, 0);
        sliderGrid.add(speedLabel, 0, 1);

        sliderGrid.add(headingSlider, 1, 0);
        sliderGrid.add(speedSlider, 1, 1);

        sliderGrid.add(headingValueLabel, 2, 0);
        sliderGrid.add(speedValueLabel, 2, 1);

        StackPane buttonPane = new StackPane();
        buttonPane.setAlignment(Pos.CENTER);
        Button sendButton = new Button(rb.getString("Send"));

        Util.addButtonEffects(sendButton);

        sendButton.setOnAction(ActionEvent -> {
            silicoGame.startCycle((int) Math.round(headingSlider.getValue()),
                    (int) Math.round(speedSlider.getValue()));
        });

        StackPane.setMargin(sendButton, new Insets(8, 8, 8, 32));

        buttonPane.getChildren().add(sendButton);

        hBox.getChildren().addAll(sliderGrid, buttonPane);
        return hBox;
    }

    private HBox addGameOverBottomBox() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        gameStateLabel = new Label("Game state");
        gameStateLabel.setFont(assets.getZorqueFontNormal());

        StackPane buttonPane = new StackPane();
        buttonPane.setAlignment(Pos.CENTER);

        Button restartButton = new Button(rb.getString("Restart"));
        Util.addButtonEffects(restartButton);
        StackPane.setMargin(restartButton, new Insets(8, 8, 8, 32));

        StackPane.setMargin(restartButton, new Insets(8, 8, 8, 32));

        restartButton.setOnAction(ActionEvent -> {
            newGame();
        });

        buttonPane.getChildren().add(restartButton);

        hBox.getChildren().addAll(gameStateLabel, buttonPane);

        return hBox;
    }

    @Override
    public void updateGui() {
        renderer.render();
        switch (silicoGame.gameState) {
            case DISABLED:
                controlBox.setVisible(false);
                gameOverBox.setVisible(true);
                gameStateLabel.setTextFill(Color.valueOf("#755973"));
                gameStateLabel.setText(rb.getString("XamDisabled"));
                break;
            case OUT_OF_REACH:
                controlBox.setVisible(false);
                gameOverBox.setVisible(true);
                gameStateLabel.setTextFill(Color.valueOf("#755973"));
                gameStateLabel.setText(rb.getString("XamOutOfReach"));
                break;
            case BASE_REACHED:
                controlBox.setVisible(false);
                gameOverBox.setVisible(true);
                gameStateLabel.setTextFill(Color.valueOf("#7abc47"));
                gameStateLabel.setText(rb.getString("BaseReached"));
                break;

        }
    }

    @Override
    public int getSpeedInput() {
        return (int) Math.round(speedSlider.getValue());
    }

    @Override
    public int getHeadingInput() {
        return (int) Math.round(headingSlider.getValue());
    }

}
