package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.spbau.mit.simpleftp.client.MyFtpClient;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class ListClient extends Client implements Runnable {

	private MyFtpClient client;
	private Path serverPathDirToCheck;
	private MyFtpRequest request;

	private static final int REQUEST_PACKAGES_NUM = 10;

	public ListClient(MyFtpClient client, Path serverPathDirToCheck, MyFtpRequest request) {
		super();
		this.client = client;
		this.serverPathDirToCheck = serverPathDirToCheck;
		this.request = request;
	}

	public void doRun() throws IOException {
		connect(client);
		Set<String> paths = Files.list(serverPathDirToCheck).map(x -> x.getFileName().toString())
				.collect(Collectors.toSet());
		for (int i = 0; i < REQUEST_PACKAGES_NUM; i++) {
			client.sendRequest(request);
			MyFtpListResponse response = client.nextMyFtpListResponse();
			List<MyFtpListResponse.Entry> contents = response.getContents();
			assertEquals(contents.size(), paths.size());
			for (MyFtpListResponse.Entry entry : contents) {
				assertTrue(paths.contains(entry.getFileName()));
				assertEquals(entry.isDir(), Files.isDirectory(serverPathDirToCheck.resolve(entry.getFileName())));
			}
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
