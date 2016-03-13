package ru.spbau.mit.simpleftp.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Paths;

import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class MyFtpSocketRequestReader {
    public static class MyFtpParseRequestException extends Exception {
        private static final long serialVersionUID = 4448519501858646038L;
    }

    private ObjectInputStream inputStream;

    public MyFtpSocketRequestReader(Socket socket) throws IOException {
        super();
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public MyFtpRequest nextMyFtpRequest() throws IOException, MyFtpParseRequestException {
        MyFtpRequest parsedRequest = new MyFtpRequest();
        int type = inputStream.readInt();
        switch (type) {
        case 1:
            parsedRequest.setType(MyFtpRequest.Type.LIST);
            break;
        case 2:
            parsedRequest.setType(MyFtpRequest.Type.GET);
            break;
        default:
            throw new MyFtpParseRequestException();
        }
        try {
            parsedRequest.setPath(Paths.get((String) inputStream.readObject()));
        } catch (ClassNotFoundException e) {
            throw new MyFtpParseRequestException();
        }
        return parsedRequest;
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
