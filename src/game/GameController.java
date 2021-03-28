// ** @author lurny
package game;

import java.util.List;

import mechanic.Field;
import mechanic.GameBoard;

public class GameController {
	private GameBoard gb;
	
	// ** setUp Gameboard with special Fields
	public GameBoard setUpGameboard(){
		this.gb = new GameBoard(15);
		List<Field> specialFields = GameSettings.getSpecialFields();
		for(Field f: specialFields) {
			this.gb.getField(f.getxCoordinate()-1, f.getyCoordinate()-1).setLetterMultiplier(f.getLetterMultiplier());
			this.gb.getField(f.getxCoordinate()-1, f.getyCoordinate()-1).setWordMultiplier(f.getWordMultiplier());
		}
		return null;
	}
	
	public GameBoard getGameBoard() {
		return this.gb;
	}
}
