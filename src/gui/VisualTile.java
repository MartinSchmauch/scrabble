package gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
public class VisualTile extends StackPane {

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
    this.setAlignment(Pos.CENTER);
    
    letterText = new Text(letter);
    valueText = new Text(String.valueOf(value));

    StackPane.setAlignment(valueText, Pos.BOTTOM_RIGHT);
    StackPane.setMargin(valueText, new Insets(0, 3, 3, 0));
    
    shape = new Rectangle();

    if (onRack) {

      this.setMaxSize(RACK_TILE_SIZE, RACK_TILE_SIZE);
      letterText.setFont(Font.font(32));
      valueText.setFont(Font.font(16));
      

      shape.heightProperty().bind(GamePanelController.getInstance().referenceSizeForRack
          .heightProperty().add(20).multiply(0.9));
      shape.widthProperty().bind(GamePanelController.getInstance().referenceSizeForRack
          .widthProperty().add(20).multiply(0.9));


      letterTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(30));
      valueTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(50));

      letterText.styleProperty()
          .bind(Bindings.concat("-fx-font-size: ", letterTextFontSize.asString(), ";"));


        valueText.styleProperty()
            .bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.asString(), ";"));

    } else {
      this.setMaxSize(BOARD_TILE_SIZE, BOARD_TILE_SIZE);

      shape.heightProperty().bind(
          GamePanelController.getInstance().referenceSizeForRack.heightProperty().multiply(0.9));
      shape.widthProperty().bind(
          GamePanelController.getInstance().referenceSizeForRack.widthProperty().multiply(0.9));

      letterTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(35));
      valueTextFontSize.bind(GamePanelController.getInstance().board.heightProperty().divide(65));

      letterText.styleProperty()
          .bind(Bindings.concat("-fx-font-size: ", letterTextFontSize.asString(), ";"));

      letterText.setFont(Font.font(26));
      valueText.setFont(Font.font(14));

        valueText.styleProperty()
            .bind(Bindings.concat("-fx-font-size: ", valueTextFontSize.asString(), ";"));
      
    }
    shape.setArcHeight(10);
    shape.setArcWidth(10);
    shape.setFill(Color.rgb(226, 189, 160));

    this.getChildren().addAll(shape, letterText, valueText);
  }

  public Rectangle getMyShape() {
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
