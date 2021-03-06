/**
 * 
 */
package com.emergya.persistenceGeo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emergya.persistenceGeo.exceptions.MultipleFilesWithSameExtension;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;

/**
 * @author adiaz
 * 
 */
public class FileUtils {

	private static final Log LOG = LogFactory.getLog(FileUtils.class);
	private static final String PUNTO = ".";

	/**
	 * Create a temporal file
	 * 
	 * @param fileName
	 * @param type
	 * 
	 * @return temporal file
	 * 
	 * @throws IOException
	 */
	public static File createFileTemp(String fileName, String type)
			throws IOException {
		String extension = null;
		String name = null;
		try {
			extension = type != null ? type.toLowerCase()
					: getDocumentExtension(fileName);
			name = type != null ? fileName : getDocumentName(fileName);
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
		return File.createTempFile(name, extension);
	}

	/**
	 * Obtiene a extension
	 * 
	 * @param path
	 * @return extension del documento
	 */
	public static String getDocumentExtension(String path) {
		String extension = null;
		try {
			extension = path.substring(path.lastIndexOf(PUNTO) + 1);
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
		return extension;
	}

	/**
	 * Obtiene el nombre del documento
	 * 
	 * @param path
	 * @return nombre del documento
	 */
	public static String getDocumentName(String path) {
		String name = null;
		try {
			name = path.substring(0, path.lastIndexOf(PUNTO));
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
		return name;
	}

	/**
	 * Unzip a file into a temporal directory an returns that directory.
	 * 
	 * @param zipFile
	 *            the file to unzip.
	 * @return the temporal directory.
	 * @throws IOException
	 */
	public static File unzipToTempDir(File zipFile) throws IOException {
		// Create temp directory
		File tempDir = Files.createTempDir();

		// get the zip content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			String fileName = ze.getName();
			if (!ze.isDirectory()) {
				final File file = new File(tempDir, fileName);
				Files.createParentDirs(file);
				Files.write(ByteStreams.toByteArray(zis), file);
			}

			ze = zis.getNextEntry();

		}
		zis.closeEntry();
		zis.close();

		return tempDir;
	}

	/**
	 * Devuelve el archivo que termina en <code>extension</code> dentro del
	 * directorio <code>parentDir</code>.
	 * 
	 * @param parentDir
	 *            directorio en el que buscar el archivo.
	 * @param extension
	 *            extensión del archivo sin punto.
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             si <code>parentDir</code> no es un directorio.
	 * @throws MultipleFilesWithSameExtension
	 *             si existe más de un archivo con <code>extension</code> en
	 *             <code>parentDir</code>.
	 */
	public static File getUniqueFileWithExtensionInDir(File parentDir,
			String extension) throws IOException, IllegalArgumentException,
			MultipleFilesWithSameExtension {
		File result = null;
		if (parentDir.isDirectory()) {
			PatternFilenameFilter filter = new PatternFilenameFilter(
					"(?i).*\\." + extension + "$");
			File[] files = parentDir.listFiles(filter);
			if (files.length > 1) {
				// Multiple files with the same extension found. Throw
				// exception.
				throw new MultipleFilesWithSameExtension(
						"Multiple files found with the same extension \"."
								+ extension + "\" in folder "
								+ parentDir.getAbsolutePath());
			} else if (files.length == 1) {
				result = files[0];
			}
		} else {
			throw new IllegalArgumentException(parentDir.getAbsolutePath()
					+ " is not a directory");
		}

		return result;

	}

}
