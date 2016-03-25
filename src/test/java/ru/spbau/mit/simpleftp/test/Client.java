package ru.spbau.mit.simpleftp.test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import ru.spbau.mit.simpleftp.client.MyFtpClient;

public abstract class Client extends MyFtpClient implements Runnable {
    private static final int RECONNECT_TIMEOUT = 100;
    private static final int RECONNECT_TRYES = 10;

    private volatile boolean result = true;

    public Client(Path configPath, PrintStream log) {
        super(configPath, log);
        // TODO Auto-generated constructor stub
    }


    public void connectingLoop() throws IOException {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
            for (int i = RECONNECT_TRYES; i >= 0; i--) {
                try {
                    reconnect();
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


    public abstract void doRun() throws IOException;

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            result = false;
        }
    }

    public boolean getResult() {
        return result;
    }
}
