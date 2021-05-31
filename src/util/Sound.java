package util;

import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * This class provides methods to play sounds from the sounds folder.
 *
 * @author lurny
 */
public class Sound {
  private static float volume = 1f;

  /**
   * plays a sound, when a tile is moved.
   */
  public static void playStartGameSound() {
    playMedia("/sounds/start.mp3");
  }


  /**
   * plays a sound, when a tile is moved.
   */
  public static void playMoveTileSound() {
    playMedia("/sounds/move.mp3");
  }

  /**
   * plays a sound, when a player commits a successful turn.
   */
  public static void playSuccessfulTurnSound() {
    playMedia("/sounds/sucess.mp3");
  }

  /**
   * plays a sound, when a player commits an unsuccessful turn.
   */
  public static void playUnsuccessfulTurnSound() {
    playMedia("/sounds/invalidTurn.mp3");
  }
  
  /**
   * plays a sound, when a player commits an unsuccessful turn.
   */
  public static void playEndGameSound() {
    playMedia("/sounds/end.mp3");
  }

  /**
   * plays a sound if a player joins the lobby.
   */
  public static void playJoin() {
    playMedia("/sounds/join.mp3");
  }

  /**
   * playes a sound id a player leaves.
   */
  public static void playLeave() {
    playMedia("/sounds/leave.mp3");
  }
  
  /**
   * plays sound if yourself leaves.
   */
  
  public static void playSelfLeave() {
    playMedia("/sounds/leaveSelf.mp3");
  }

  /**
   * plays the wav file saved at url filepath.
   */
  public static void playMedia(String url) {
    try {
      URL resource = Sound.class.getResource(url);
      Media media = new Media(resource.toString());
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      mediaPlayer.setVolume(volume);
      mediaPlayer.play();
    } catch (Exception e) {
      System.out.println("Sound disabled. Please check whether proper System Requirements are met.");
    }
  }

  /**
   * set the static volume variable.
   */
  public static void setVolume(int newVolume) {
    volume = (float) (((float) newVolume) / 100f);
  }

}
