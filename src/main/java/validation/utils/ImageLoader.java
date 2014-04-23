package validation.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ImageLoader {

	private ImageLoader() {
	}

	public static Image getImage(Class<?> relativeClass, String filename) {
		Image returnValue = null;
		InputStream is = relativeClass.getResourceAsStream(filename);
		if (is != null) {
			try {
				returnValue = Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(is));
			} catch (IOException exception) {
				System.err.println("Error loading: " + filename);
			}
		}
		return returnValue;
	}
}