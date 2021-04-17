package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class VisualTile extends Parent {

  private static final int TILE_WIDTH = 55;
  private static final int TILE_HEIGHT = 55;

  private static Rectangle shape;

  public static Rectangle getShape() {
    return shape;
  }

  private static Text letterText;
  private static Text valueText;


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
    double leftMargin = TILE_WIDTH - letterText.getLayoutBounds().getWidth();
    double topMargin = TILE_HEIGHT - letterText.getLayoutBounds().getHeight();

    sP.getChildren().addAll(shape, letterText, valueText);
    sP.setAlignment(Pos.CENTER);
    StackPane.setMargin(valueText, new Insets(topMargin, 0, 0, leftMargin));

    getChildren().add(sP);
  }


  public static void setShape(Rectangle shape) {
    VisualTile.shape = shape;
  }

  public static Text getLetterText() {
    return letterText;
  }

  public static void setLetterText(Text letterText) {
    VisualTile.letterText = letterText;
  }

  public static Text getValueText() {
    return valueText;
  }

  public static void setValueText(Text valueText) {
    VisualTile.valueText = valueText;
  }

}
