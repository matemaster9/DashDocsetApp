package org.metanoia.dashdocsetapp;

import java.nio.file.Path;

import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * @author cyborg
 * @since 2024-06-30 04:38
 */
@Getter
public class WebJarDownloadSuccessEvent extends ApplicationEvent {

	private final Path webJarPath;

	public WebJarDownloadSuccessEvent(Object source, Path webJarPath) {
		super(source);
		this.webJarPath = webJarPath;
	}
}
