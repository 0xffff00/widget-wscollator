package hzk.widgets.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHashUtils {
	private static Log log = LogFactory.getLog(FileHashUtils.class);
	private static final int BUFFER_SIZE_OF_BYTE = 65536;
	public static final String ALGORITHM_SHA = "SHA-1";
	public static final String ALGORITHM_MD5 = "MD5";

	public static String simpleSHA1(File file) {
		byte[] buffer = new byte[BUFFER_SIZE_OF_BYTE];
		InputStream ins = null;
		try {
			ins = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance(ALGORITHM_SHA);
			int nread = 0;
			while ((nread = ins.read(buffer)) != -1) {
				md.update(buffer, 0, nread);
			}
			return toHexString(md.digest());
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error(null, e);
			return null;
		} finally {
			IOUtils.closeQuietly(ins);
		}

	}

	public static String toHexString(byte[] b) {
		if (b == null) {
			return null;
		}
		StringBuilder hex = new StringBuilder();
		int x, y, i;
		char p, q;
		for (i = 0; i < b.length; i++) {
			x = b[i] >> 4 & 15;
			y = b[i] & 15;
			if (x > 9)
				p = (char) (x + 87);
			else
				p = (char) (x + 48);
			if (y > 9)
				q = (char) (y + 87);
			else
				q = (char) (y + 48);
			hex.append(p).append(q);
		}

		return hex.toString();

	}
}
