package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import ru.spbau.mit.simpleftp.client.MyFtpClient;
import ru.spbau.mit.simpleftp.common.MyFtpListResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class ListClientFail extends Client implements Runnable {
    private MyFtpClient client;
    private MyFtpRequest request;

    private static final int REQUEST_PACKAGES_NUM = 10;

    public ListClientFail(MyFtpClient client, MyFtpRequest request) {
        super();
        this.client = client;
        this.request = request;
    }

    public void doRun() throws IOException {
        connect(client);
        for (int i = 0; i < REQUEST_PACKAGES_NUM; i++) {
            client.sendRequest(request);
            MyFtpListResponse response = client.nextMyFtpListResponse();
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
