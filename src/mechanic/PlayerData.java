package mechanic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/** @author ldreyer */
public class PlayerData {
	private int id;
	private String nickname;
	private Image avatar;
	// private GameStatistic Statistic;

	public PlayerData(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Image getAvatar() {
		return avatar;
	}

	/** @author nilbecke */
	public void setAvatar(String input) {
		try {
			BufferedImage bi = ImageIO.read(new File(input));
			avatar = convert(bi);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @author nilbecke
	 * @param input of type Buffered Image
	 * @return A Java FX Image
	 */
	public Image convert(BufferedImage input) {
		WritableImage wr = null;
		if (input != null) {
			wr = new WritableImage(input.getWidth(), input.getHeight());
			PixelWriter pw = wr.getPixelWriter();
			for (int x = 0; x < input.getWidth(); x++) {
				for (int y = 0; y < input.getHeight(); y++) {
					pw.setArgb(x, y, input.getRGB(x, y));
				}
			}
		}
		return (Image) wr;
	}

	public void setAvatar(Image input) {
		this.avatar = input;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
}
