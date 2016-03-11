package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import ru.spbau.mit.simpleftp.client.MyFtpClient;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;
import ru.spbau.mit.simpleftp.server.MyFtpServer;

public class TestMyFtp {
	private final static Path CONFIG_DIR = Paths.get("res/conf");
	private final static Path CLIENT_CONF = CONFIG_DIR.resolve("client.conf");
	private final static Path DB_CONF = CONFIG_DIR.resolve("db.conf");

	private final static Path FTP_FILES = Paths.get("res/ftp_files");
	private final static Path DOWNLOAD_FILES = Paths.get("res/download_files");

	private final static Path LIST_CLIENT_REQUEST = Paths.get("");
	private final static Path LIST_CLIENT_FAIL_REQUEST = Paths.get("NOPE");
	private final static Path GET_CLIENT_REQUEST = Paths.get("pushkin/serv");
	private final static Path GET_CLIENT_DOWNLOAD_PATH = DOWNLOAD_FILES.resolve(Paths.get("newserv"));
	private final static Path GET_CLIENT_FAIL_REQUEST = DOWNLOAD_FILES.resolve("NOPE");

	private final static int SERVER_INIT_WAIT_TIME = 5000;

	private MyFtpClient newClient() {
		return new MyFtpClient(CLIENT_CONF, System.out);
	}

	private MyFtpServer newServer() {
		return new MyFtpServer(DB_CONF, new ServerLogStream(System.err), System.in);
	}

	private void doJoin(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMyFtpWithConnection() {
		Thread serverThread = new Thread(new Server(newServer()));
		serverThread.start();
		/*
		 * i don't want to synchronize server with client - we just wait for a
		 * long, reconnect if failed with connect for some times and wait again
		 */
		try {
			Thread.sleep(SERVER_INIT_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ListClient testListClient = new ListClient(newClient(), FTP_FILES,
				new MyFtpRequest(MyFtpRequest.Type.LIST, LIST_CLIENT_REQUEST));

		ListClientFail testListClientFail = new ListClientFail(newClient(),
				new MyFtpRequest(MyFtpRequest.Type.LIST, LIST_CLIENT_FAIL_REQUEST));

		GetClient testGetClient = new GetClient(newClient(), FTP_FILES.resolve(GET_CLIENT_REQUEST),
				GET_CLIENT_DOWNLOAD_PATH, new MyFtpRequest(MyFtpRequest.Type.GET, GET_CLIENT_REQUEST));

		GetClientFail testGetClientFail = new GetClientFail(newClient(), null,
				new MyFtpRequest(MyFtpRequest.Type.GET, GET_CLIENT_FAIL_REQUEST));

		Thread clientListThread = new Thread(testListClient);
		Thread clientListFailThread = new Thread(testListClientFail);
		Thread clientGetThread = new Thread(testGetClient);
		Thread clientGetFailThread = new Thread(testGetClientFail);

		clientListThread.start();
		clientListFailThread.start();
		clientGetThread.start();
		clientGetFailThread.start();

		doJoin(clientListFailThread);
		doJoin(clientListThread);
		doJoin(clientGetThread);
		doJoin(clientGetFailThread);

		assertTrue(testListClientFail.getResult());
		assertTrue(testListClient.getResult());
		assertTrue(testGetClient.getResult());
		assertTrue(testGetClientFail.getResult());
	}
}
