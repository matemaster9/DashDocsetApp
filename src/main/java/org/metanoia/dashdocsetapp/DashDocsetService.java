package org.metanoia.dashdocsetapp;

import java.io.IOException;
import java.nio.file.Path;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author cyborg
 * @since 2024-06-30 04:32
 */
public interface DashDocsetService {

	/**
	 * Downloads a JAR file from the web.
	 *
	 * @param webJar The URL of the JAR file to download.
	 */
	void downloadJarFromWeb(String webJar);

	/**
	 * Converts a given JAR file to a Dash docset format.
	 *
	 * @param jar The path to the JAR file to be converted.
	 */
	void convertJarToDashDocset(Path jar);

	/**
	 * Downloads a JAR file from a specified URL, converts it to a Dash docset, and sends the resulting docset
	 * directly in the HTTP response.
	 *
	 * @param webJar   The URL of the JAR file to download and convert.
	 * @param response The HTTP response object to which the converted docset will be written.
	 */
	void convertWebJarToDashDocset(String webJar, HttpServletResponse response);

	/**
	 * Downloads the specified docset file and writes it to the HTTP response.
	 *
	 * @param docset   the URL or path of the docset file to download
	 * @param response the {@link HttpServletResponse} to which the docset file will be written
	 * @throws IOException if an input or output exception occurs while processing the docset file
	 */
	@Deprecated
	void downloadDocset(String docset, HttpServletResponse response) throws IOException;
}
