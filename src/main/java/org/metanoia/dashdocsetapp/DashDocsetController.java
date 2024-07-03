package org.metanoia.dashdocsetapp;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "DashDocsetController", description = "docset生成转化")
public class DashDocsetController {

	private final DashDocsetService dashDocsetService;

	@Operation(summary = "autoConvertJarToDashDocset", description = "自动化转化jar成docset到默认存储位置")
	@PostMapping("auto-convert-jar-to-dash-docset")
	public void autoConvertJarToDashDocset(
			@Parameter(name = "jar", description = "jar的下载链接地址")
			@RequestParam("jar") String jar) {
		dashDocsetService.downloadJarFromWeb(jar);
	}

	@Operation(summary = "batchAutoConvertJarToDashDocset", description = "批量自动化转化jar成docset到默认存储位置")
	@PostMapping("batch-auto-convert-jar-to-dash-docset")
	public void batchAutoConvertJarToDashDocset(@RequestBody List<String> jars) {
		jars.forEach(dashDocsetService::downloadJarFromWeb);
	}

	@Operation(summary = "convertWebJarToDashDocset", description = "同步转化jar成docset到响应流")
	@PostMapping("convert-web-jar-to-dash-docset")
	public void convertWebJarToDashDocset(
			@Parameter(name = "webjar", description = "jar的下载链接地址")
			@RequestParam("webjar") String webjar,
			HttpServletResponse response) {
		dashDocsetService.convertWebJarToDashDocset(webjar, response);
	}

	@Operation(summary = "downloadDocset", description = "下载服务器本地的docset", deprecated = true)
	@Deprecated
	@PostMapping("download-docset")
	public void downloadDocset(HttpServletResponse response,
			@Parameter(name = "docset", description = "docset名称")
			@RequestParam("docset") String docset) throws IOException {
		dashDocsetService.downloadDocset(docset, response);
	}
}
