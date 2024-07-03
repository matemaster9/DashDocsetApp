package org.metanoia.dashdocsetapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author cyborg
 * @since 2024-07-01 21:43
 */
@Slf4j
public class CommonsIOUtils {

	/**
	 * Deletes a directory if it exists.
	 * <p>
	 * This method checks if the provided path represents a directory and if it exists.
	 * If both conditions are met, the directory and all its contents are deleted.
	 *
	 * @param directory the path to the directory to be deleted
	 * @throws IOException if an I/O error occurs during the deletion process
	 */
	public static void deleteDirectoryIfExist(Path directory) {
		try {
			if (Objects.isNull(directory)) {
				return;
			}
			if (Files.isDirectory(directory) && Files.exists(directory)) {
				FileUtils.deleteDirectory(directory.toFile());
			}
		}
		catch (IOException e) {
			log.warn("CommonsIOUtils.deleteDirectoryIfExist error", e);
		}
	}

	/**
	 * Copies data from an InputStream to a file and automatically closes both streams.
	 * <p>
	 * This method takes an InputStream and a Path representing the output file. It creates a new OutputStream for the target file and uses Apache Commons IO's IOUtils.copy method to transfer data between the streams.
	 * Finally, it closes both streams using a try-with-resources block to ensure proper resource management and prevent resource leaks.
	 *
	 * @param input  the InputStream containing the data to be copied
	 * @param output the path to the output file
	 * @throws IOException if an I/O error occurs during the copy operation
	 */
	public static void copyAndAutoClose(InputStream input, Path output) {
		try (OutputStream outputStream = Files.newOutputStream(output)) {
			IOUtils.copy(input, outputStream);
		}
		catch (IOException e) {
			log.warn("CommonsIOUtils.copyAndAutoClose error", e);
		}
	}
}
