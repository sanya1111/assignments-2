package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.common.MyFtpGetResponse;
import ru.spbau.mit.simpleftp.common.MyFtpRequest;

public class GetClientFail extends Client {
    private Path downloadFilePath;
    private MyFtpRequest request;

    private static final int REQUEST_PACKAGES_NUM = 1;

    public GetClientFail(Path configPath, PrintStream log, Path downloadPath, MyFtpRequest request) {
        super(configPath, log);
        this.request = request;
        this.downloadFilePath = downloadPath;
    }

    @Override
    public void doRun() throws IOException {
        connectingLoop();
        for (int i = 0; i < REQUEST_PACKAGES_NUM; i++) {
            sendRequest(request);
            MyFtpGetResponse response = nextMyFtpGetResponse(downloadFilePath);
            assertTrue(response.isFailed());
        }
        closeSocket();
    }
}
