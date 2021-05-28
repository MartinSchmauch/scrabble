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

  /*
   * plays a sound, when a tile is moved.
   */
  public static void playStartGameSound() {
    playMedia("/sounds/start.mp3");
  }


  /*
   * plays a sound, when a tile is moved.
   */
  public static void playMoveTileSound() {
    playMedia("/sounds/move.mp3");
  }

  /*
   * plays a sound, when a player commits a successful turn.
   */
  public static void playSuccessfulTurnSound() {
    playMedia("/sounds/sucess.mp3");
  }

  /*
   * plays a sound, when a player commits an unsuccessful turn.
   */
  public static void playUnsuccessfulTurnSound() {
    playMedia("/sounds/invalidTurn.mp3");
  }

  /**
   * plays the wav file saved at url filepath.
   */
  public static void playMedia(String url) {
    URL resource = Sound.class.getResource(url);
    System.out.println(resource.toString());
    Media media = new Media(resource.toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setVolume(volume);
    mediaPlayer.play();
  }

  public static void setVolume(int newVolume) {
    volume = (float) (((float) newVolume) / 100f);
    System.out.println("volume" + volume);
  }

}
