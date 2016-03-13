package ru.spbau.mit.simpleftp.client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;

public class MyFtpSocketResponseReader {
    private ObjectInputStream socketInputStream;

    public MyFtpSocketResponseReader(Socket socket) throws IOException {
        super();
        socketInputStream = new ObjectInputStream(socket.getInputStream());
    }

    private static final int READ_WRITE_BLOCK_SIZE = 4096;

    public MyFtpGetResponse nextMyFtpGetResponse(Path newFileLocation) throws IOException {
        MyFtpGetResponse response = new MyFtpGetResponse();
        response.setPath(newFileLocation);
        response.setSize(socketInputStream.readLong());

        if (!response.isFailed()) {
            byte[] readWriteBlock = new byte[READ_WRITE_BLOCK_SIZE];
            try (BufferedOutputStream fileStream = new BufferedOutputStream(
                    new FileOutputStream(response.getPath().toString()))) {
                int gotBytes = 0;
                for (Long index = 0L; index < response.getSize(); index += gotBytes) {
                    gotBytes = socketInputStream.read(readWriteBlock);

                    if (gotBytes == -1) {
                        throw new IOException();
                    }

                    fileStream.write(readWriteBlock, 0, gotBytes);
                }
            }
        }
        return response;
    }

    public MyFtpListResponse nextMyFtpListResponse() throws IOException {
        MyFtpListResponse response = new MyFtpListResponse();
        int size = socketInputStream.readInt();
        for (int index = 0; index < size; index++) {
            String gotFileName;
            try {
                gotFileName = (String) socketInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException();
            }
            boolean isDir = socketInputStream.readBoolean();
            response.addEntry(new MyFtpListResponse.Entry(gotFileName, isDir));
        }
        return response;
    }

    public void close() throws IOException {
        socketInputStream.close();
    }

}
