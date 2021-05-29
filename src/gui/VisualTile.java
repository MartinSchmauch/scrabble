package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * This class represents a tile on the MainGamePanelScreen and the constructor is being called, when
 * a new tile needs to be genereated. The tile consists of a Parent container, which contains a
 * StackPane. In the StackPane a rectangle and two texts are layered.
 *
 * @author mschmauc
 */
public class VisualTile extends Parent {

  private static final int RACK_TILE_SIZE = 60;
  private static final int BOARD_TILE_SIZE = 50;

  private Rectangle shape;
  private Text letterText;
  private Text valueText;

  private DoubleProperty letterTextFontSize = new SimpleDoubleProperty(10);
  private DoubleProperty valueTextFontSize = new SimpleDoubleProperty(10);

  /**
   * This method creates an instance of the class.
   */
  public VisualTile(String letter, int value, boolean onRack) {
    StackPane pane = new StackPane();
    pane.setAlignment(Pos.CENTER);

    letterText = new Text(letter);
    valueText = new Text(String.valueOf(value));
    

    
    shape = new Rectangle(RACK_TILE_SIZE - 5, RACK_TILE_SIZE - 5);

    if (onRack) {
      
//      pane.setMaxSize(RACK_TILE_SIZE, RACK_TILE_SIZE);
//      letterText.setFont(Font.font(32));
//      valueText.setFont(Font.font(16));
      
      letterTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(45));
      valueTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(75));
      
      letterText.styleProperty().bind(Bindings.concat("-fx-font-size: ", letterTextFontSize.asString(), ";"));
      
      if (letter.equals("Q")) {
        valueText.styleProperty().bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.subtract(2).asString(), ";"));
      }
      else {
        valueText.styleProperty().bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.asString(), ";"));
      }
      
    } else {
//      pane.setMaxSize(BOARD_TILE_SIZE, BOARD_TILE_SIZE);
      
      letterTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(55));
      valueTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(85));
      
      
//      letterText.setFont(Font.font(26));
//      valueText.setFont(Font.font(14));
      if (letter.equals("Q")) {
        valueText.styleProperty().bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.subtract(2).asString(), ";"));
      }
      else {
        valueText.styleProperty().bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.asString(), ";"));
      }
    }
    shape.setArcHeight(10);
    shape.setArcWidth(10);
    shape.setFill(Color.rgb(226, 189, 160));

    pane.getChildren().addAll(shape, letterText, valueText);

    StackPane.setAlignment(valueText, Pos.BOTTOM_RIGHT);

    double rightMargin = 3;
    double bottomMargin = 3;
    StackPane.setMargin(valueText, new Insets(0, bottomMargin, rightMargin, 0));

    getChildren().add(pane);
  }

  public Rectangle getShape() {
    return shape;
  }

  public void setShape(Rectangle shape) {
    this.shape = shape;
  }

  public Text getLetterText() {
    return this.letterText;
  }

  public void setLetterText(Text letterText) {
    this.letterText = letterText;
  }

  public Text getValueText() {
    return this.valueText;
  }

  public void setValueText(Text valueText) {
    this.valueText = valueText;
  }

  /**
   * @return the tileFontSize
   */
  public DoubleProperty getTileFontSize() {
    return letterTextFontSize;
  }

  /**
   * @param tileFontSize the tileFontSize to set
   */
  public void setTileFontSize(DoubleProperty tileFontSize) {
    this.letterTextFontSize = tileFontSize;
  }

}
