package ru.spbau.mit.simpleftp.test;

import java.io.IOException;

import ru.spbau.mit.simpleftp.client.MyFtpClient;

public abstract class Client {
	private static final int RECONNECT_TIMEOUT = 100;
	private static final int RECONNECT_TRYES = 10;

	public void connect(MyFtpClient client) throws IOException {
		try {
			client.connect();
		} catch (IOException e) {
			e.printStackTrace();
			for (int i = RECONNECT_TRYES; i >= 0; i--) {
				try {
					client.reconnect();
					return;
				} catch (Exception e2) {
					e2.printStackTrace();
					try {
						Thread.sleep(RECONNECT_TIMEOUT);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			throw new IOException();
		}
	}

	public abstract boolean getResult();
}
