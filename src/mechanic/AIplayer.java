package mechanic;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.*;

/**
 * 
 * @author pkoenig
 *
 */
public class AIplayer extends Player {

  // private String[] rowStrings; //regex friendly boardletters
  // private String[] colStrings; //regex friendly boardletters

  public AIplayer(String nickname) {
    super(nickname);
    // TODO Auto-generated constructor stub
  }

  @JsonCreator
  public AIplayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume);
  }

  // public int[][] getValidPositionsForWordLength(Field[][] fields, int wordlength) {
  // if (wordlength <= 0) {
  // return null;
  // }
  // int[][] res = new int[fields.length][fields.length];
  // for (int j = 0; j < fields.length; j++) {
  // for (int i = 0; i < fields.length; i++) {
  // if (i >= 1 && fields[i - 1][j].getTile() != null) {
  // res[i][j] = 1;
  // } else if (j >= 1 && fields[i][j - 1].getTile() != null) {
  // res[i][j] = 1;
  // } else if (i < fields.length - 1 && fields[i + 1][j].getTile() != null) {
  // res[i][j] = 1;
  // } else if (j < fields.length - 1 && fields[i][j + 1].getTile() != null) {
  // res[i][j] = 1;
  // }
  // }
  //
  // }
  // for (int j = 0; j < res.length; j++) {
  // for (int i = 0; i < res.length; i++) {
  // if (res[i][j] == 1) {
  //
  // }
  // }
  //
  // }
  // for (int j = 0; j < fields.length; j++) {
  // for (int i = 0; i < fields.length; i++) {
  // System.out.print(res[i][j]);
  // }
  // System.out.println();
  // }
  // return res;
  //
  // }

  public ArrayList<Field[]> getValidWordPositionsForWordLength(GameBoard gb, int wordlength) {
    if (wordlength <= 0) {
      return null;
    }
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    Field[] singleWordPosition = new Field[wordlength];
    for (int j = 1; j <= gb.getFields().length; j++) {
      for (int i = 1; i <= gb.getFields().length; i++) {
        if (gb.getField(i, j).getTop() != null && gb.getField(i, j).getTop().getTile() != null
            || gb.getField(i, j).getRight() != null && gb.getField(i, j).getRight().getTile() != null
            || gb.getField(i, j).getBottom() != null && gb.getField(i, j).getBottom().getTile() != null
            || gb.getField(i, j).getLeft() != null && gb.getField(i, j).getLeft().getTile() != null) {
          // horizontal
          for (int k = i - wordlength; k < i; k++) {
            if (!(k < 1) && !(k + wordlength > gb.getFields().length)) {
              for (int z = 0; z < wordlength; z++) {
                singleWordPosition[z] = gb.getField(k + z + 1, j);
              }
              results.add(singleWordPosition);
              singleWordPosition = new Field[wordlength];
            }
          }
          // Vertical
          for (int k = j - wordlength; k < j; k++) {
            if (!(k < 1) && !(k + wordlength > gb.getFields().length)) {
              for (int z = 0; z < wordlength; z++) {
                singleWordPosition[z] = gb.getField(i, k + z + 1);
              }
              results.add(singleWordPosition);
              singleWordPosition = new Field[wordlength];
            }
          }
        }
      }

    }

    System.out.println("\n ACTUAL");
    for (int i = 0; i < results.size(); i++) {
      System.out.println("\n" + results.get(i));
      for (Field f : results.get(i)) {
        System.out.println(f.toString());
      }
      
    }
    return results;

  }

}
