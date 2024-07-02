package org.metanoia.dashdocsetapp;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemProperties;
import org.junit.jupiter.api.Test;

/**
 * @author cyborg
 * @since 2024-07-01 21:25
 */
public class CommonsIOTest {


	@Test
	void concat() {
		System.out.println(FilenameUtils.concat("/Users/metanoia/Documents", "test"));
		System.out.println(FilenameUtils.concat("/Users/metanoia/Documents", ".test"));
	}

	@Test
	void name() {
		System.out.println(SystemProperties.getUserDir());
	}
}
