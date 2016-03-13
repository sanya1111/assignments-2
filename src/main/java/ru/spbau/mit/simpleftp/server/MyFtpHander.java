package ru.spbau.mit.simpleftp.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;
import ru.spbau.mit.simpleftp.common.MyFtpResponse;

public class MyFtpHander implements Runnable {
    private Socket socket;
    private PrintStream log;
    private MyFtpSocketRequestReader requestReader = null;
    private MyFtpSocketResponseWriter responseWriter = null;
    private Path rootDir;

    MyFtpHander(Socket socket, PrintStream log, Path rootDir) {
        this.socket = socket;
        this.log = log;
        this.rootDir = rootDir;
    }

    @Override
    public void run() {
        try {
            requestReader = new MyFtpSocketRequestReader(socket);
            responseWriter = new MyFtpSocketResponseWriter(socket);
        } catch (IOException e) {
            e.printStackTrace(log);
            return;
        }

        String hostAddr = socket.getInetAddress().getHostAddress();

        log.println(String.format("New client %s have connected", hostAddr));

        for (int requestId = 0;; requestId++) {
            MyFtpRequest request = null;
            try {
                request = requestReader.nextMyFtpRequest().resolvePathWithRootDir(rootDir);
            } catch (IOException e) {
                e.printStackTrace(log);
                break;
            } catch (MyFtpSocketRequestReader.MyFtpParseRequestException e) {
                e.printStackTrace(log);
                continue;
            }

            log.println(String.format("Client %s send %d request %s", hostAddr, requestId, request.toString()));

            MyFtpResponse response = dispatchResponse(request);
            try {
                responseWriter.writeMyFtpResponse(response);
            } catch (IOException e) {
                e.printStackTrace(log);
            }
            log.println(String.format("Response to client %s to %d request have sended", hostAddr, requestId));
        }

        log.println(String.format("Client %s have disconnected", hostAddr));
        closeSocket();
    }

    public void closeSocket() {
        if (requestReader != null) {
            try {
                requestReader.close();
            } catch (IOException e) {
                e.printStackTrace(log);
            }
        }

        if (responseWriter != null) {
            try {
                responseWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(log);
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace(log);
            }
        }
    }

    MyFtpResponse dispatchResponse(MyFtpRequest request) {
        switch (request.getType()) {
        case LIST:
            return getResponseFromListResponse(request);
        default:
            return getResponseFromGetResponse(request);
        }
    }

    public MyFtpListResponse getResponseFromListResponse(MyFtpRequest request) {
        MyFtpListResponse response = new MyFtpListResponse();
        if (!Files.isDirectory(request.getPath())) {
            log.println(request.getPath().toString() + " isn't dir");
            response.setFailed();
            return response;
        }
        try {
            Files.list(request.getPath()).forEach(filePath -> {
                response.addEntry(new MyFtpListResponse.Entry(filePath.getFileName().toString(),
                        Files.isDirectory(filePath)));
            });
        } catch (IOException e) {
            response.setFailed();
            e.printStackTrace(log);
        }
        return response;
    }

    public MyFtpGetResponse getResponseFromGetResponse(MyFtpRequest request) {
        MyFtpGetResponse response = new MyFtpGetResponse();
        if (!Files.exists(request.getPath())) {
            log.println(request.getPath().toString() + " isn't exists");
            response.setFailed();
            return response;
        }
        response.setPath(request.getPath());
        try {
            response.setSize(Files.size(request.getPath()));
        } catch (IOException e) {
            e.printStackTrace(log);
            response.setFailed();
        }
        return response;
    }

}
