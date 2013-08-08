package hzk.widgets.utils;

import static org.junit.Assert.*;
import hzk.widgets.util.ImageTransformUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageTransformUtilsTest {

	/**
	 *   For example mergeImagesFilesToOneVertically("d:\\doc",03,4,50,150)
	 *  thus,some jpg files call'03.jpg'...'06.jpg' under 'd:/doc' dir will be merged to one file vertically.
	 * @deprecated too high-coupled coding
	 * @param path
	 * @param start first image file name(must be numeric)
	 * @param len source  image files count
	 * @param y1 the start edge's Y axis to cut
	 * @param y2 the end edge's Y axis to cut
	 * @throws IOException
	 */
	public static void mergeImagesFilesToOneVertically(String path, int start, int len,
			int y1, int y2) throws IOException {
		int n=len;
		File[] imgFiles = new File[n];
		BufferedImage[] imgs = new BufferedImage[n];
		for (int i = 0; i < n; i++) {
			imgFiles[i] = new File(path + "\\" + to2digits(i+start) + ".jpg");
			imgs[i] = ImageIO.read(imgFiles[i]);
		}
		int type = imgs[0].getType();
		int width = imgs[0].getWidth();
		BufferedImage finalImg = new BufferedImage(width, (y2 - y1) * n, type);
		Graphics2D g = finalImg.createGraphics();
		int w = width;
		for (int i = 0, y = 0; i < n; i++) {
			g.setColor(Color.gray);

			g.drawImage(imgs[i], 0, y, w, y + y2 - y1, 0, y1, w, y2, null);
			// g.drawLine(0, y, w, y);
			g.drawLine(0, y, 5, y + 3);
			g.drawLine(0, y + 6, 5, y + 3);
			y += y2 - y1;
		}

		ImageIO.write(finalImg, "jpeg", new File(path + "\\merged_"
				+ to2digits(start) + "-" + to2digits(start + n - 1) + ".jpg"));
		System.out.println("done");
	}

	static String to2digits(int i) {
		return i < 10 ? "0" + i : "" + i;
	}

	static String to3digits(int i) {
		return i < 10 ? "00" + i : (i < 100 ? "0" + i : "" + i);
	}
	
	@Test
	public void test() {
		int y1 = 124, y2 = 604;
		String path1 = "E:\\Pictures\\���� Nokia 800\\fenlei";
		try {
			mergeImagesFilesToOneVertically(path1, 1, 5, y1, y2);
			mergeImagesFilesToOneVertically(path1, 1, 15, y1, y2);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	 
}
