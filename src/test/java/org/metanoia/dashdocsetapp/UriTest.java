package org.metanoia.dashdocsetapp;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

/**
 * @author cyborg
 * @since 2024-06-30 04:50
 */
public class UriTest {

	@Test
	void uri() {
		URI uri = URI.create("https://javadoc.io/jar/com.fasterxml.jackson.core/jackson-databind/2.17.1/jackson-databind-2.17.1-javadoc.jar");
		System.out.println("Scheme: " + uri.getScheme());
		System.out.println("Authority: " + uri.getAuthority());
		System.out.println("User Info: " + uri.getUserInfo());
		System.out.println("Host: " + uri.getHost());
		System.out.println("Port: " + uri.getPort());
		System.out.println("Path: " + uri.getPath());
		System.out.println("Query: " + uri.getQuery());
		System.out.println("Fragment: " + uri.getFragment());
	}


	@Test
	void getFileName() {
		URI uri = URI.create("https://javadoc.io/jar/com.fasterxml.jackson.core/jackson-databind/2.17.1/jackson-databind-2.17.1-javadoc.jar");
		String filename = Paths.get(uri.getPath()).getFileName().toString();
		System.out.println(filename);
		System.out.println(FilenameUtils.getBaseName(filename));
	}

	@Test
	void name() {
		System.out.println(Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar").getFileName());
	}
}
