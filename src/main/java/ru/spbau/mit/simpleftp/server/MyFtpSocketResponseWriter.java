package ru.spbau.mit.simpleftp.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;
import ru.spbau.mit.simpleftp.common.MyFtpResponse;

public class MyFtpSocketResponseWriter {
	private ObjectOutputStream objectStream;

	public MyFtpSocketResponseWriter(Socket socket) throws IOException {
		super();
		objectStream = new ObjectOutputStream(socket.getOutputStream());
	}

	private void writeMyFtpListResponse(MyFtpListResponse response) throws IOException {
		objectStream.writeInt(response.getContents().size());
		for (MyFtpListResponse.Entry entry : response.getContents()) {
			objectStream.writeObject(entry.getFileName());
			objectStream.writeBoolean(entry.isDir());
		}
		objectStream.flush();
	}

	private static final int READ_WRITE_BLOCK_SIZE = 4096;

	private void writeMyFtpGetResponse(MyFtpGetResponse response) throws IOException {
		MyFtpGetResponse getResponse = (MyFtpGetResponse) response;
		objectStream.writeLong(getResponse.getSize());
		objectStream.flush();
		/* old staff */
		if (!response.isFailed()) {
			byte readWriteBlock[] = new byte[READ_WRITE_BLOCK_SIZE];
			try (BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(response.getPath().toString()))) {
				while (true) {
					int gotBytes = stream.read(readWriteBlock);

					if (gotBytes == -1) {
						break;
					}

					objectStream.write(readWriteBlock, 0, gotBytes);
					objectStream.flush();
				}
			}
		}
	}

	public void writeMyFtpResponse(MyFtpResponse response) throws IOException {
		if (response instanceof MyFtpListResponse) {
			writeMyFtpListResponse((MyFtpListResponse) response);
		} else {
			writeMyFtpGetResponse((MyFtpGetResponse) response);
		}
	}

	public void close() throws IOException {
		objectStream.close();
	}
}
