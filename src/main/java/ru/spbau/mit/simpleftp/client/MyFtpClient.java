package ru.spbau.mit.simpleftp.client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;
import ru.spbau.mit.simpleftp.etc.ConfigParser;

public class MyFtpClient {
	private static final String HOST_DEFAULT = "127.0.0.1";
	private static final int PORT_DEFAULT = 6666;

	private String host;
	private int port;
	private Socket socket;
	private MyFtpSocketRequestWriter socketWriter;
	private MyFtpSocketResponseReader socketReader;
	private PrintStream log;

	void parseConfig(Path configPath) throws IOException {
		Map<String, List<String>> parsed = ConfigParser.parseConfig(configPath);
		if (parsed.containsKey("host")) {
			host = parsed.get("host").get(0);
		}
		if (parsed.containsKey("port")) {
			port = Integer.parseInt(parsed.get("port").get(0));
		}
	}

	private void setupDefauls() {
		host = HOST_DEFAULT;
		port = PORT_DEFAULT;
	}

	public MyFtpClient(Path configPath, PrintStream log) {
		this.log = log;
		setupDefauls();
		if (configPath != null) {
			try {
				parseConfig(configPath);
			} catch (IOException e) {
				e.printStackTrace(log);
				setupDefauls();
			}
		}
	}

	public synchronized void connect() throws UnknownHostException, IOException {
		socket = new Socket(InetAddress.getByName(host), port);
		socketWriter = new MyFtpSocketRequestWriter(socket);
		socketReader = new MyFtpSocketResponseReader(socket);
	}

	public synchronized void sendRequest(MyFtpRequest request) throws IOException {
		socketWriter.writeMyFtpRequest(request);
	}

	public synchronized MyFtpListResponse nextMyFtpListResponse() throws IOException {
		return socketReader.nextMyFtpListResponse();
	}

	public synchronized MyFtpGetResponse nextMyFtpGetResponse(Path newFileLocation) throws IOException {
		return socketReader.nextMyFtpGetResponse(newFileLocation);
	}

	public synchronized void closeSocket() throws IOException {
		if (socketReader != null) {
			socketReader.close();
		}
		if (socketWriter != null) {
			socketWriter.close();
		}
		if (socket != null) {
			socket.close();
		}
		socketWriter = null;
		socketReader = null;
		socket = null;
	}

	public synchronized void reconnect() throws IOException {
		closeSocket();
		connect();
	}
}
