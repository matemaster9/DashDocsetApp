package org.metanoia.dashdocsetapp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class DashDocsetAppApplicationTests {

	@Autowired
	DashDocsetService dashDocsetService;

	@Test
	void contextLoads() {
		dashDocsetService.downloadJarFromWeb("https://javadoc.io/jar/commons-io/commons-io/2.16.1/commons-io-2.16.1-javadoc.jar");
	}
}
