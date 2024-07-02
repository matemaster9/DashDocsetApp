package org.metanoia.dashdocsetapp;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

/**
 * @author cyborg
 * @since 2024-06-30 11:16
 */
public class ApacheCommonsExecTest {

	@Test
	void convert() throws IOException {
		CommandLine generateDocset = CommandLine.parse("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/javadocset");
		generateDocset.addArgument("commons-exec-1.4.0-javadoc");
		generateDocset.addArgument("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/commons-exec-1.4.0-javadoc");

		CommandLine removeJavaDoc = CommandLine.parse("rm");
		removeJavaDoc.addArgument("-rf");
		removeJavaDoc.addArgument("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/commons-exec-1.4.0-javadoc");

		DefaultExecutor defaultExecutor = DefaultExecutor.builder().get();
		int execute = defaultExecutor.execute(generateDocset);

		if (execute == 0) {
			defaultExecutor.execute(removeJavaDoc);
		}
	}

	@Test
	void execZshFile() throws IOException {
		CommandLine zsh = CommandLine.parse("/bin/zsh");

		zsh.addArgument("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/auto.sh");

		DefaultExecutor defaultExecutor = DefaultExecutor.builder()
				.setWorkingDirectory(Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar").toFile())
				.get();

		defaultExecutor.execute(zsh);
	}


	@Test
	void ls() throws IOException {
		CommandLine zsh = CommandLine.parse("/bin/zsh");

		zsh.addArgument("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/ls.sh");

		DefaultExecutor defaultExecutor = DefaultExecutor.builder()
				.setWorkingDirectory(Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar").toFile())
				.get();

		defaultExecutor.execute(zsh);
	}

	@Test
	void path() {
		Path path = Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar/commons-exec-1.4.0-javadoc.jar");
		System.out.println(path.getFileName().toString());
		System.out.println(path.toAbsolutePath());
		System.out.println(FilenameUtils.getFullPathNoEndSeparator(path.toAbsolutePath().toString()));
		System.out.println(Path.of("/Users/cyborg/IntelliJIDEAProjects/DashDocsetApp/dash-docset/webjar").getFileName().toString());
	}

	@Test
	void name() {
		CommandLine commandLine = CommandLine.parse("/app/bin/javadocset");
		commandLine.addArgument("commons-exec-1.4.0");
		commandLine.addArgument("/app/temp/webjar/3242384234234/commons-exec-1.4.0-javadoc");

		System.out.println(commandLine);
	}
}
