package org.metanoia.dashdocsetapp;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author cyborg
 * @since 2024-06-30 04:47
 */
@Service
@Slf4j
public class DashDocsetServiceImpl implements DashDocsetService {

	private final ApplicationEventPublisher applicationEventPublisher;

	private final DashDocsetDirConfigProperties dashDocsetDirConfigProperties;

	private final CloseableHttpAsyncClient httpAsyncClient;

	private final CloseableHttpClient httpClient;

	@Autowired
	public DashDocsetServiceImpl(ApplicationEventPublisher applicationEventPublisher,
			DashDocsetDirConfigProperties dashDocsetDirConfigProperties) {
		this.httpClient = HttpClients.createDefault();
		this.httpAsyncClient = HttpAsyncClients.createDefault();
		this.applicationEventPublisher = applicationEventPublisher;
		this.dashDocsetDirConfigProperties = dashDocsetDirConfigProperties;
	}

	@Override
	public void downloadJarFromWeb(String webJar) {
		try {
			URI uri = URI.create(webJar);
			Path tempPathForWebJar = createTempPathForWebJar(uri);

			SimpleHttpRequest request = SimpleRequestBuilder.get(uri).build();
			httpAsyncClient.start();
			httpAsyncClient.execute(request, getDownloadJarFromWebFutureCallBack(tempPathForWebJar));
		}
		catch (IllegalArgumentException ex) {
			log.error("Error downloading jar from web", ex);
		}
		catch (IOException ex) {
			log.error("Error create jar file path", ex);
		}
	}

	private FutureCallback<SimpleHttpResponse> getDownloadJarFromWebFutureCallBack(Path tempPathForWebJar) {
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse response) {

				CommonsIOUtils.copyAndAutoClose(new ByteArrayInputStream(response.getBodyBytes()), tempPathForWebJar);

				if (dashDocsetDirConfigProperties.getEnablePublishWebJarDownloadSuccessEvent()) {
					publishWebJarDownloadSuccessEvent(tempPathForWebJar);
				}
			}

			@Override
			public void failed(Exception e) {
				log.error("Error downloading jar from web", e);
			}

			@Override
			public void cancelled() {
				log.debug("Cancelled web jar download");
			}
		};
	}

	private void publishWebJarDownloadSuccessEvent(Path tempPathForWebJar) {
		applicationEventPublisher.publishEvent(new WebJarDownloadSuccessEvent(this, tempPathForWebJar));
	}


	private Path createTempPathForWebJar(URI uri) throws IOException {
		String filename = Paths.get(uri.getPath()).getFileName().toString();
		String webJarTempStorePath = dashDocsetDirConfigProperties.getWebJarTempStorePath();
		String webJarTempPath = webJarTempStorePath + File.separator + Instant.now().toEpochMilli() + File.separator;
		Path tempDirPath = Path.of(webJarTempPath);

		// Ensure parent directories exist
		if (!Files.exists(tempDirPath)) {
			Files.createDirectories(tempDirPath);
		}
		return tempDirPath.resolve(filename);
	}

	@Override
	public void convertJarToDashDocset(Path jar) {
		try {
			Path decompressedPath = decompressJar(jar);

			if (Objects.nonNull(decompressedPath)) {
				// exec javadocset: jar -> docset
				generateDocset(decompressedPath);

				// move docset -> docset-store-path
				String src = decompressedPath.toAbsolutePath() + ".docset";
				String dest = FilenameUtils.concat(
						dashDocsetDirConfigProperties.getDocsetStorePath(),
						decompressedPath.getFileName().toString() + ".docset"
				);

				moveJavaDocsetToStorePath(src, dest);
			}
		}
		finally {
			CommonsIOUtils.deleteDirectoryIfExist(jar.getParent());
		}
	}


	private void moveJavaDocsetToStorePath(String docset, String dest) {
		try {
			Path desetPath = Path.of(dest);
			if (Files.exists(desetPath)) {
				FileUtils.deleteDirectory(desetPath.toFile());
			}
			Path docsetPath = Path.of(docset);
			if (Files.exists(docsetPath)) {
				Files.move(docsetPath, desetPath);
			}
		}
		catch (IOException e) {
			log.error("move docset to store path error", e);
		}
	}

	private Path decompressJar(Path jar) {
		Path decompressedPath = null;
		try (JarArchiveInputStream jarIn = createArchiveInputStreamFor(jar)) {

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
					CommonsIOUtils.copyAndAutoClose(jarIn, entryDestination);
				}
			}
		}
		catch (IOException e) {
			log.error("Decompress error", e);
		}
		return decompressedPath;
	}

	private JarArchiveInputStream createArchiveInputStreamFor(Path jar) throws IOException {
		InputStream inputStream = Files.newInputStream(jar);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		return new JarArchiveInputStream(bufferedInputStream);
	}

	private Path getDecompressedPath(Path jar) {
		String absolutePath = jar.toAbsolutePath().toString();

		String decompressedPathStr;
		if (StringUtils.endsWith(absolutePath, "-javadoc.jar")) {
			decompressedPathStr = StringUtils.substringBeforeLast(absolutePath, "-javadoc.jar");
		}
		else {
			decompressedPathStr = StringUtils.substringAfterLast(absolutePath, ".jar");
		}
		return Path.of(decompressedPathStr);
	}

	private void generateDocset(Path dir) {
		try {
			String absolutePath = dir.toFile().getAbsolutePath();
			String baseName = dir.getFileName().toString();
			File workingDir = dir.getParent().toFile();

			CommandLine cmdLine = new CommandLine(dashDocsetDirConfigProperties.getJavadocsetTool());
			cmdLine.addArgument(baseName);
			cmdLine.addArgument(absolutePath);

			DefaultExecutor executor = DefaultExecutor.builder()
					.setWorkingDirectory(workingDir)
					.get();

			int execute = executor.execute(cmdLine);
			if (execute == 0) {
				log.debug("exec javadocset generate success");
			}
			else {
				log.error("exec javadocset generate failed");
			}
		}
		catch (IOException e) {
			log.error("Error generating javadocset", e);
		}
	}

	@Override
	public void convertWebJarToDashDocset(String webjar, HttpServletResponse response) {
		try {
			URI uri = URI.create(webjar);

			Path tempPathForWebJar = createTempPathForWebJar(uri);

			downloadWebJarToTempPath(uri, tempPathForWebJar);

			Path decompressedPath = decompressJar(tempPathForWebJar);

			if (Objects.nonNull(decompressedPath)) {
				generateDocset(decompressedPath);

				Path docsetPath = Path.of(decompressedPath.toAbsolutePath() + ".docset");

				writeDocsetToHttpServletResponse(docsetPath, response);
			}

			CommonsIOUtils.deleteDirectoryIfExist(tempPathForWebJar.getParent());
		}
		catch (IOException e) {
			log.error("Error generating webjar", e);
		}
	}

	private void writeDocsetToHttpServletResponse(Path docsetPath, HttpServletResponse response) {
		response.setContentType("application/gzip");
		response.setHeader("Content-Disposition", "attachment; filename=\"docset.tgz\"");

		try (TarArchiveOutputStream tarOutputStream = createTarArchiveOutputStreamFor(response.getOutputStream())) {
			tarOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			addFilesToTarArchive(docsetPath, tarOutputStream);
		}
		catch (IOException e) {
			log.error("write docset to HttpServletResponse error", e);
		}
	}

	private void downloadWebJarToTempPath(URI uri, Path tempPathForWebJar) {
		HttpGet httpGet = new HttpGet(uri);
		HttpHost httpHost = HttpHost.create(uri);

		try (ClassicHttpResponse classicHttpResponse = httpClient.executeOpen(httpHost, httpGet, null);
			 InputStream inputStream = classicHttpResponse.getEntity().getContent()) {
			IOUtils.copy(inputStream, Files.newOutputStream(tempPathForWebJar));
			EntityUtils.consume(classicHttpResponse.getEntity());
		}
		catch (IOException e) {
			log.error("Error downloading webjar", e);
		}
	}

	private TarArchiveOutputStream createTarArchiveOutputStreamFor(ServletOutputStream outputStream) throws IOException {
		GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(outputStream);
		return new TarArchiveOutputStream(gzipOutputStream);
	}

	private void addFilesToTarArchive(Path base, TarArchiveOutputStream tarOutputStream) {
		try {
			// add base
			String baseDir = base.getFileName().toString() + File.separator;
			TarArchiveEntry baseEntry = new TarArchiveEntry(baseDir);
			tarOutputStream.putArchiveEntry(baseEntry);
			tarOutputStream.closeArchiveEntry();

			Collection<File> files = FileUtils.listFiles(base.toFile(), null, true);

			files.forEach(item -> addFileToTarArchive(base, item, baseDir, tarOutputStream));

			tarOutputStream.finish();
		}
		catch (IOException e) {
			log.error("Error adding files to tar archive", e);
		}
	}

	private void addFileToTarArchive(Path base, File file, String baseDir, TarArchiveOutputStream tarOutputStream) {
		try {
			String relativePath = baseDir + base.relativize(file.toPath());
			TarArchiveEntry entry = new TarArchiveEntry(relativePath);
			entry.setSize(file.length());
			tarOutputStream.putArchiveEntry(entry);
			try (InputStream inputStream = new FileInputStream(file)) {
				IOUtils.copy(inputStream, tarOutputStream);
			}
			tarOutputStream.closeArchiveEntry();
		}
		catch (IOException e) {
			log.warn("Error adding file to tar archive", e);
		}
	}

	@Override
	public void downloadDocset(String docset, HttpServletResponse response) throws IOException {
		if (StringUtils.isEmpty(docset)) {
			throw new IllegalArgumentException("docset is empty");
		}

		File docsetStoreDir = new File(dashDocsetDirConfigProperties.getDocsetStorePath());
		Collection<File> docsetFiles = FileUtils.listFilesAndDirs(docsetStoreDir,
				TrueFileFilter.INSTANCE,
				FileFilterUtils.suffixFileFilter(".docset"));

		Optional<File> first = docsetFiles.stream()
				.filter(file -> StringUtils.contains(file.getName(), docset))
				.findFirst();

		if (first.isPresent()) {
			writeDocsetToHttpServletResponse(first.get().toPath(), response);
		}
		else {
			throw new IOException("docset not found");
		}
	}
}
