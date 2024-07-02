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

	public static void copyAndAutoClose(InputStream input, Path output) {
		try (OutputStream outputStream = Files.newOutputStream(output)) {
			IOUtils.copy(input, outputStream);
		}
		catch (IOException e) {
			log.warn("CommonsIOUtils.copyAndAutoClose error", e);
		}
	}
}
