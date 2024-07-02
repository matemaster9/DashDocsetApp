package org.metanoia.dashdocsetapp;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cyborg
 * @since 2024-06-30 04:28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dash")
public class DashDocsetController {

	private final DashDocsetService dashDocsetService;

	@PostMapping("auto-convert-jar-to-dash-docset")
	public void autoConvertJarToDashDocset(@RequestParam("jar") String jar) {
		dashDocsetService.downloadJarFromWeb(jar);
	}

	@PostMapping("batch-auto-convert-jar-to-dash-docset")
	public void batchAutoConvertJarToDashDocset(@RequestBody List<String> jars) {
		jars.forEach(dashDocsetService::downloadJarFromWeb);
	}

	@PostMapping("convert-web-jar-to-dash-docset")
	public void convertWebJarToDashDocset(@RequestParam("webjar") String webjar, HttpServletResponse response) {
		dashDocsetService.convertWebJarToDashDocset(webjar, response);
	}

	@Deprecated
	@PostMapping("download-docset")
	public void downloadDocset(HttpServletResponse response, @RequestParam("docset") String docset) throws IOException {
		dashDocsetService.downloadDocset(docset, response);
	}
}
