package gui;



/**
 * Deprecated!
 * 
 * @author mschmauc
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestGamePanel extends Application {

  public static void main(String[] args) {
    launch();
  }

  @SuppressWarnings("unchecked")
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Scrabble");
    VBox parent = new VBox(); // parent container


    Label headline1 = new Label("Chat"); // label
    parent.getChildren().add(headline1); // label zu parent hinzufügen

    TextField chatField = new TextField(); // textfeld
    parent.getChildren().add(chatField);

    Button sendButton = new Button("Send"); // knopf
    sendButton.setPrefWidth(200);
    sendButton.setPrefHeight(20);
    parent.getChildren().add(sendButton);

    TextArea chatMessages = new TextArea("Chat Area: \n hallo"); // textfeld-bearbeitbar
    chatMessages.setTranslateY(50); // um 50px nach unten verschieben
    parent.getChildren().add(chatMessages);

    Label headline2 = new Label("Wähle eine Zahl aus!");
    parent.getChildren().add(headline2);

    Slider slider = new Slider(); // schieberegler
    slider.setMax(360);
    parent.getChildren().add(slider);

    TextField number = new TextField();
    number.rotateProperty().bind(slider.valueProperty()); // number textfield drehen lassen
    // number.opacityProperty().bind(slider.valueProperty()); //Transparenz
    // number.scaleYProperty().bind(slider.valueProperty()); //Stauchen und Strecken
    parent.getChildren().add(number);

    number.textProperty().bind(slider.valueProperty().asString());

    Label headline3 = new Label("Punktestand: ");
    parent.getChildren().add(headline3);

    // diagramm
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> punkte = new BarChart<String, Number>(xAxis, yAxis);

    XYChart.Series<String, Number> series1 = new XYChart.Series<>();
    XYChart.Data<String, Number> data1 = new XYChart.Data<String, Number>("Spieler 1", 23);
    XYChart.Data<String, Number> data2 = new XYChart.Data<String, Number>("Spieler 2", 17);
    series1.getData().addAll(data1, data2);
    series1.setName("Punkte nach erstem Spiel");
    punkte.getData().add(series1);

    // punkte.setTranslateY(20);
    parent.getChildren().add(punkte);

    // listener, der etwas macht, wenn der slider sich ändert -> siehe changed() methode
    slider.valueProperty().addListener(new ChangeListener<Number>() {

      @Override
      public void changed(ObservableValue<? extends Number> property, Number oldValue,
          Number newValue) {
        if (newValue.doubleValue() > (slider.getMax() - 310)) {
          number.textProperty().unbind();
          number.setText("Der Wert vom Slider ist über 50, nämlich: " + newValue);
        } else {
          number.textProperty().bind(slider.valueProperty().asString());
        }
      }

    });

    // MouseListener für Chat Senden Knopf -> wenn geklickt, wird nachricht aus chatField
    // hinzugefügt
    sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent click) {
        chatMessages.textProperty().setValue(
            chatMessages.textProperty().getValue() + "\n" + chatField.textProperty().getValue());
        chatField.textProperty().setValue("");
      }

    });

    // chatMessages.textProperty().bind(number.textProperty());
    // chatMessages.textProperty().bind(chatField.textProperty()); //auch bindBidirectional moeglich

    Scene scene = new Scene(parent); // ein buenenbild mit 'parent' als inhalt
    primaryStage.setScene(scene);

    primaryStage.show();
  }
}
