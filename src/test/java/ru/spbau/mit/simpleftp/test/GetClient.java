package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.client.MyFtpClient;
import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;

import org.apache.commons.io.FileUtils;

public class GetClient extends Client implements Runnable {

	private MyFtpClient client;
	private Path serverPathFileToCheck;
	private Path downloadFilePath;
	private MyFtpRequest request;

	private static final int REQUEST_PACKAGES_NUM = 1;

	public GetClient(MyFtpClient client, Path serverPathDirToCheck, Path downloadPath, MyFtpRequest request) {
		super();
		this.client = client;
		this.serverPathFileToCheck = serverPathDirToCheck;
		this.request = request;
		this.downloadFilePath = downloadPath;
	}

	public void doRun() throws IOException {
		connect(client);
		File srcFile = new File(serverPathFileToCheck.toString());
		for (int i = 0; i < REQUEST_PACKAGES_NUM; i++) {
			client.sendRequest(request);
			MyFtpGetResponse response = client.nextMyFtpGetResponse(downloadFilePath);
			assertFalse(response.isFailed());
			File gotFile = new File(response.getPath().toString());
			assertTrue(FileUtils.contentEquals(srcFile, gotFile));
		}
		client.closeSocket();
	}

	private volatile boolean result = true;

	@Override
	public void run() {
		try {
			doRun();
		} catch (Exception | AssertionError e) {
			e.printStackTrace();
			result = false;
		}
	}

	@Override
	public boolean getResult() {
		return result;
	}

}
