package hzk.widgets.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * 
 * @author HZK
 * 
 */
public class JImagePane extends JPanel {
	private static final long serialVersionUID = -8251916094895167058L;

	public static final int NORMAL = 0, CENTER = 1, TILED = 2, SCALED = 4;

	private String title;
	private int title_x, title_y;
	private Color title_fore_color;

	int width;
	int height;
	int bgImageWidth;
	int bgImageHeight;

	private Image bgImage;
	private Image scaledBgImage;

	private int displayMode;

	public JImagePane() {
		this(null, NORMAL);
	}

	public JImagePane(Image image) {
		this(image, NORMAL);
	}

	public JImagePane(Image image, int displayMode) {
		this(image, displayMode, image.getWidth(null), image.getHeight(null));
	}

	public JImagePane(Image image, int displayMode, int w, int h) {
		super();
		setSize(w, h);
		setDisplayMode(displayMode);
		setImageTitle("", 3, 12, Color.WHITE);
		loadBackgroundImage(image);
	}

	public void setImageTitle(String title) {
		this.title = title;

	}

	public void setImageTitle(String title, int x, int y, Color c) {
		this.title = title;
		title_x = x;
		title_y = y;
		title_fore_color = c;
	}

	public void loadBackgroundImage(Image image) {
		this.bgImage = image;
		if (image!=null){
		bgImageWidth = bgImage.getWidth(this);
		bgImageHeight = bgImage.getHeight(this);
		if (displayMode==SCALED){
			scaledBgImage = bgImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
		}
		}
		this.repaint();
	}

	public Image getBackgroundImage() {
		return bgImage;
	}

	public void setDisplayMode(int displayMode) {
		 this.displayMode=displayMode;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bgImage == null)
			return;
		switch (displayMode) {
		case NORMAL:
			g.drawImage(bgImage, 0, 0, this);
			break;
		case CENTER:
			int x1 = (width - bgImageWidth) / 2;
			int y1 = (height - bgImageHeight) / 2;

			g.drawImage(bgImage, x1, y1, this);
			break;
		case TILED:
			for (int x = 0; x < width; x += bgImageWidth) {
				for (int y = 0; y < height; y += bgImageHeight) {
					g.drawImage(bgImage, x, y, this);
				}
			}
			break;
		case SCALED:
			g.drawImage(scaledBgImage, 0, 0, this);
			break;
		}

		if (title != null && !title.isEmpty()) {
			g.setColor(title_fore_color);
			g.drawString(title, title_x, title_y);
		}
	}

}
