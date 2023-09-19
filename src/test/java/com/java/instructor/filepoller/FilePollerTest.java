package com.java.instructor.filepoller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.core.util.FileUtil;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class FilePollerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePollerTest.class);

	@Autowired
	private ApplicationContext applicationContext;

	public static String INPUT_DIR;
	public static String OUTPUT_DIR;
	public static String TEST_FILE_DIR;

	// preparing the directories structure
	@BeforeClass
	public static void prepareDirectoryStructure() throws IOException {
		String currentPath = Paths.get("").toAbsolutePath().toString();
		INPUT_DIR = currentPath + File.separator + "input";
		OUTPUT_DIR = currentPath + File.separator + "output";
		TEST_FILE_DIR = currentPath + File.separator + "testfiles";
		FileUtil.createMissingParentDirectories(new File(INPUT_DIR));
		FileUtil.createMissingParentDirectories(new File(OUTPUT_DIR));
	}

	// Cleanup directories
	@AfterClass
	public static void cleanup() throws IOException {
		FileUtils.cleanDirectory(new File(INPUT_DIR));
		FileUtils.cleanDirectory(new File(OUTPUT_DIR));
		FileUtils.deleteDirectory(new File(INPUT_DIR));
		FileUtils.deleteDirectory(new File(OUTPUT_DIR));
	}

	@Test(timeout = 6000) // timeout may differ number of channels flows the messages.
	public void copyFileToSourceDirAndTest() throws Exception {
		PollableChannel testLogHandlerChannel = applicationContext.getBean("testLogHandlerChannel",
				PollableChannel.class);
		FileUtils.copyDirectory(new File(TEST_FILE_DIR), new File(INPUT_DIR));
		Message<?> msg = testLogHandlerChannel.receive();
		LOGGER.info(" Junit test verification for payload ::" + msg.getPayload());
		assertThat(msg.getPayload(), is(notNullValue()));

	}

}
