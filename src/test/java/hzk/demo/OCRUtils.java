package hzk.demo;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//import net.sourceforge.tess4j.*;

public class OCRUtils {

	
	public static String recognizeTimeForLumia800Screenshot(File file){
		
		try {
			BufferedImage img=ImageIO.read(file);
			return recognizeTimeForLumia800Screenshot(img);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Rectangle timeZone= new Rectangle(420, 3, 54, 25);
	//static Tesseract instance = Tesseract.getInstance();
	public static String recognizeTimeForLumia800Screenshot(BufferedImage image) {

//		try {
//			String result = instance
//					.doOCR(image,timeZone);
//			// System.out.println(result);
//			return result.trim();
//		} catch (TesseractException e) {
//			System.err.println(e.getMessage());
//			return null;
//		}
		return null;
	}
	
}