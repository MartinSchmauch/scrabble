package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * 
 * @author Martin
 * 
 *         This class represents a tile on the MainGamePanelScreen and the constructor is being
 *         called, when a new tile needs to be genereated. The tile consists of a Parent container,
 *         which contains a StackPane. In the StackPane a rectangle and two texts are layered.
 */
public class VisualTile extends Parent {

  private static final int TILE_WIDTH = 55;
  private static final int TILE_HEIGHT = 55;

  private Rectangle shape;

  public Rectangle getShape() {
    return shape;
  }

  private Text letterText;
  private Text valueText;


  public VisualTile(String letter, int value, boolean onRack) {
    StackPane sP = new StackPane();
    sP.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
    if (onRack) {
      shape = new Rectangle(TILE_WIDTH, TILE_HEIGHT);
    } else {
      shape = new Rectangle(TILE_WIDTH - 10, TILE_HEIGHT - 10);
    }
    shape.setArcHeight(10);
    shape.setArcWidth(10);
    shape.setFill(Color.rgb(226, 189, 160));

    letterText = new Text(letter);
    letterText.setFont(Font.font(32));

    valueText = new Text(String.valueOf(value));
    valueText.setFont(Font.font(16));
    double rightMargin = 4.5;
    double bottomMargin = 4.5;

    sP.getChildren().addAll(shape, letterText, valueText);
    sP.setAlignment(Pos.CENTER);
    StackPane.setAlignment(valueText, Pos.BOTTOM_RIGHT);
    StackPane.setMargin(valueText, new Insets(0, bottomMargin, rightMargin, 0));

    getChildren().add(sP);
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

}
