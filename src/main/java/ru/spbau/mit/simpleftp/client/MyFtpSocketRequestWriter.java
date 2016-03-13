package ru.spbau.mit.simpleftp.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class MyFtpSocketRequestWriter {
    private ObjectOutputStream objectStream;

    public MyFtpSocketRequestWriter(Socket socket) throws IOException {
        super();
        objectStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void writeMyFtpRequest(MyFtpRequest request) throws IOException {
        switch (request.getType()) {
        case LIST:
            objectStream.writeInt(1);
            break;
        case GET:
            objectStream.writeInt(2);
            break;
        default:
            break;
        }
        objectStream.writeObject(request.getPath().toString());
        objectStream.flush();
    }

    public void close() throws IOException {
        objectStream.close();
    }
}
