package org.metanoia.dashdocsetapp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.SystemProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.metanoia.dashdocsetapp.DashDocsetDirConfigProperties.prefix;

/**
 * @author cyborg
 * @since 2024-06-30 04:43
 */
@Setter
@EqualsAndHashCode
@ToString
@ConfigurationProperties(prefix = prefix)
public class DashDocsetDirConfigProperties {

	public static final String prefix = "dash-docset";

	private String docsetStorePath;

	private String webJarTempStorePath;

	private String javadocsetTool;

	private String zshWorkingDir;

	@Getter
	private Boolean enablePublishWebJarDownloadSuccessEvent;

	private final String userDir = SystemProperties.getUserDir();

	public String getDocsetStorePath() {
		return userDir + docsetStorePath;
	}

	public String getWebJarTempStorePath() {
		return userDir + webJarTempStorePath;
	}

	public String getJavadocsetTool() {
		return userDir + javadocsetTool;
	}

	public String getZshWorkingDir() {
		return userDir + zshWorkingDir;
	}
}
