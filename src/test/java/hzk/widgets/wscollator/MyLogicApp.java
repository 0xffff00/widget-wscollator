package hzk.widgets.wscollator;

import hzk.demo.OCRUtils;
import hzk.widgets.util.ImageTransformUtils;
import hzk.widgets.util.MyIOUtils;
import hzk.widgets.util.FileHashUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * trivial logic for batch executions
 * @author HZK
 *
 */
public class MyLogicApp {
	final static String JPG = "jpg";
	private static Log log = LogFactory.getLog(MyLogicApp.class);

	static String pathwx = "E:\\Pictures\\来自 Nokia 800\\wx";
	static String pathwx2 = "E:\\Pictures\\来自 Nokia 800\\wx2";
	static String path2 = "E:\\Pictures\\来自 Nokia 800\\tt";
	
	/**
	 * 
	 * @param time
	 *            eg. "12:34"
	 * @return eg. "1234"
	 */
	static String format(String time) {
		if (!time.contains(":"))
			return null;
		String[] s = time.split(":");
		if (s.length != 2)
			return null;
		String mm = "", ss = "";
		if (StringUtils.isNumeric(s[0]))
			mm = MyIOUtils.to2digits(s[0]);
		else
			return null;
		if (StringUtils.isNumeric(s[1]))
			ss = MyIOUtils.to2digits(s[1]);
		else
			return null;

		return mm + ss;
	}

	public static void main(String[] args) {
		// logic_ocr_zone_test_view(path2);
		// logic_rename_all_wx_screenshots(pathwx2);
		// logic_rename_bad_images_files_again(pathwx2+"\\bad");
		//logic_clip_pics(pathwx);
	}

	public static void logic_clip_pics(String path) {
		int w0 = 480, h0 = 800;
		int left = 10, right = 10, top = 124, bottom = 72;
		int w = w0 - left - right, h = h0 - top - bottom;
		File dir = new File(path);
		File dir_clipped = new File(path + "//clipped");
		if (!dir_clipped.exists()) {
			dir_clipped.mkdir();
		}

		Collection<File> files = FileUtils.listFiles(dir, new String[] { JPG }, false);
		int n=0;
		for (File f : files) {
			try {
				BufferedImage img0 = ImageIO.read(f);
				if (w0==img0.getWidth() && h0==img0.getHeight()){
					BufferedImage img = ImageTransformUtils.clip(img0, left, top, w, h);
					ImageIO.write(img, "jpeg", new File(path + "//clipped//" + f.getName()));
					log.info(n+++":\t"+f.getName());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void logic_ocr_zone_test_view(String path) {
		File dir1 = new File(path);
		Collection<File> files = FileUtils.listFiles(dir1, new String[] { JPG }, false);
		File f = files.iterator().next();
		try {
			BufferedImage img = ImageIO.read(f);

			BufferedImage part = ImageTransformUtils.clip(img, 420, 3, 54, 25);
			ImageIO.write(part, "jpeg", new File(path + "\\" + "time_zone.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void logic_rename_bad_images_files_again(String path) {
		File dir1 = new File(path);
		Collection<File> files = FileUtils.listFiles(dir1, new String[] { JPG }, false);
		for (File f : files) {
			int x = f.getName().indexOf('_');
			String s1 = f.getName().substring(0, x);
			String s2 = f.getName().substring(x);
			if (s1.length() == 5) {
				String nnm = s1.substring(0, 2) + s1.substring(3, 5) + s2;
				MyIOUtils.renameFileSafely(f, nnm, null);
				log.info(path + "\\" + nnm);
			}
		}
	}

	public static void logic_rename_all_wx_screenshots(String path) {

		File dir1 = new File(path);
		Collection<File> files = FileUtils.listFiles(dir1, new String[] { JPG }, false);
		int n = 0, nbad = 0;
		for (File f : files) {
			String time = OCRUtils.recognizeTimeForLumia800Screenshot(f);
			String fname = format(time);
			String pth = path;
			n++;
			if (fname == null) {
				pth = pth + "\\bad";
				fname = time;
				nbad++;
			}
			String sha1 = FileHashUtils.simpleSHA1(f);
			String newname = fname + "_" + sha1.substring(0, 12) + ".jpg";

			log.info(n + ":" + pth + "\\" + newname);

			MyIOUtils.renameFileSafely(f, newname, pth);
		}
		log.info("all files count: " + n + " ,bad files count: " + nbad);
	}
}
