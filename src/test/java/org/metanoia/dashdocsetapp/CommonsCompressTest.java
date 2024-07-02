package org.metanoia.dashdocsetapp;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * @author cyborg
 * @since 2024-06-30 19:45
 */
@Slf4j
public class CommonsCompressTest {

	public static void extractJar(Path jar) throws IOException {
		Path decompressedPath = null;
		try (InputStream inputStream = Files.newInputStream(jar);
			 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			 JarArchiveInputStream jarIn = new JarArchiveInputStream(bufferedInputStream)) {

			decompressedPath = Files.createDirectories(getDecompressedPath(jar));

			// 逐个解压JAR文件中的条目
			JarArchiveEntry entry;
			while ((entry = jarIn.getNextEntry()) != null) {
				if (!jarIn.canReadEntryData(entry)) {
					continue;
				}

				// 构建输出文件路径
				Path entryDestination = decompressedPath.resolve(entry.getName());

				if (entry.isDirectory()) {
					Files.createDirectories(entryDestination);
				}
				else {
					// 确保目录存在
					Files.createDirectories(entryDestination.getParent());

					// 写入文件内容
					try (FileOutputStream fileOutputStream = new FileOutputStream(entryDestination.toFile())) {
						IOUtils.copy(jarIn, fileOutputStream);
					}
				}
			}
		}
		catch (IOException e) {
			log.error("Decompress error", e);
			throw e;
		}
	}

	private static Path getDecompressedPath(Path jar) throws IOException {
		String fileName = jar.getFileName().toString();
		String dirName = fileName.substring(0, fileName.lastIndexOf(".jar"));
		Path parentDir = jar.getParent();
		return parentDir.resolve(dirName);
	}

	@Test
	void unzipJar() throws IOException {
		extractJar(Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/commons-exec-1.4.0-javadoc.jar"));
	}
}
