package org.metanoia.dashdocsetapp;

import java.nio.file.Path;

import lombok.AllArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author cyborg
 * @since 2024-06-30 04:40
 */
@Component
@AllArgsConstructor
public class WebJarDownloadSuccessEventListener {

	private final DashDocsetService dashDocsetService;

	@EventListener
	public void onWebJarDownloadSuccessEvent(WebJarDownloadSuccessEvent webJarDownloadSuccessEvent) {
		Path webJarPath = webJarDownloadSuccessEvent.getWebJarPath();
		dashDocsetService.convertJarToDashDocset(webJarPath);
	}
}
