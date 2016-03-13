package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.client.MyFtpClient;
import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class GetClientFail extends Client implements Runnable {
    private MyFtpClient client;
    private Path downloadFilePath;
    private MyFtpRequest request;

    private static final int REQUEST_PACKAGES_NUM = 1;

    public GetClientFail(MyFtpClient client, Path downloadPath, MyFtpRequest request) {
        super();
        this.client = client;
        this.request = request;
        this.downloadFilePath = downloadPath;
    }

    public void doRun() throws IOException {
        connect(client);
        for (int i = 0; i < REQUEST_PACKAGES_NUM; i++) {
            client.sendRequest(request);
            MyFtpGetResponse response = client.nextMyFtpGetResponse(downloadFilePath);
            assertTrue(response.isFailed());
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
