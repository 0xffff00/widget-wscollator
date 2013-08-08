package hzk.widgets.util;

import java.io.File;

public class MyIOUtils {
	/**
	 * rename the file but covering (when the same named file exists already) is
	 * prohibited. if a file of new name has already existed ,do nothing and
	 * return null
	 * 
	 * @param file
	 * @param newName
	 * @param path
	 *            if specified,than use this instead of current path of the
	 *            file.
	 * @return a file instance holding new name or null if rename failed
	 */
	public static File renameFileSafely(File file, String newName, String path) {
		String p = path;
		if (file == null || newName == null) {
			return null;
		}
		if (path == null) {
			p = file.getPath();
			int x = p.lastIndexOf('\\');
			p = p.substring(0, x);
		}

		File nf = new File(p + "\\" + newName);
		if (!nf.exists()) {
			file.renameTo(nf);
			return nf;
		} else {
			// String dt =
			// DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(Calendar.getInstance());
			// file.renameTo(new File(p + "\\" + newName + dt));
			return null;
		}
	}

	/**
	 * "0"->"01", "12"->"12"
	 * 
	 * @param numStr
	 * @return
	 */
	public static String to2digits(String numStr) {
		if (numStr.length() == 1)
			return "0" + numStr;
		else
			return numStr;
	}

	public static String to3digits(String numStr) {
		if (numStr == null || numStr.isEmpty())
			return "000";
		if (numStr.length() == 1)
			return "00" + numStr;
		if (numStr.length() == 2)
			return "0" + numStr;
		return numStr;
	}

}
