package org.metanoia.dashdocsetapp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.hc.client5.http.HttpResponseException;
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
import org.junit.jupiter.api.Test;

/**
 * @author cyborg
 * @since 2024-06-30 19:22
 */
public class HttpcomponentsClient5Test {


	private final CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault();

	@Test
	void sync() {
		String url = "https://javadoc.io/jar/commons-io/commons-io/2.16.1/commons-io-2.16.1-javadoc.jar";

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(url);

			HttpHost httpHost = HttpHost.create(URI.create(url));

			String filename = Paths.get(URI.create(url).getPath()).getFileName().toString();
			String webJarTempPath = "/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar" + File.separator + Instant.now().toEpochMilli();
			Path tempDirPath = Paths.get(webJarTempPath);

			// Ensure parent directories exist
			if (!Files.exists(tempDirPath)) {
				Files.createDirectories(tempDirPath);
			}

			Path tempPathForWebJar = tempDirPath.resolve(filename);

			try (ClassicHttpResponse response = httpClient.executeOpen(httpHost, httpGet, null)) {
				if (response.getCode() != 200) {
					throw new HttpResponseException(response.getCode(), "Failed to download file");
				}

				try (InputStream inputStream = response.getEntity().getContent();
					 FileOutputStream fileOutputStream = new FileOutputStream(tempPathForWebJar.toFile())) {
					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, bytesRead);
					}
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	void async() {
		String url = "https://javadoc.io/jar/commons-io/commons-io/2.16.1/commons-io-2.16.1-javadoc.jar";
		downloadFileAsync(url);
	}

	public void downloadFileAsync(String url) {
		try {
			httpAsyncClient.start();

			String filename = Paths.get(URI.create(url).getPath()).getFileName().toString();
			String webJarTempPath = "/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar" + File.separator + Instant.now().toEpochMilli();
			Path tempDirPath = Paths.get(webJarTempPath);

			// Ensure parent directories exist
			if (!Files.exists(tempDirPath)) {
				Files.createDirectories(tempDirPath);
			}

			Path tempPathForWebJar = tempDirPath.resolve(filename);

			SimpleRequestBuilder requestBuilder = SimpleRequestBuilder.get(url);

			httpAsyncClient.execute(requestBuilder.build(), new FutureCallback<>() {
				@Override
				public void completed(SimpleHttpResponse response) {
					try (InputStream inputStream = new ByteArrayInputStream(response.getBodyBytes());
						 FileOutputStream fileOutputStream = new FileOutputStream(tempPathForWebJar.toFile())) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = inputStream.read(buffer)) != -1) {
							fileOutputStream.write(buffer, 0, bytesRead);
						}
					}
					catch (IOException ignored) {
					}
				}

				@Override
				public void failed(Exception ex) {

				}

				@Override
				public void cancelled() {
				}
			});

		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
