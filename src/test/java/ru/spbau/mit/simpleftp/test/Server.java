package ru.spbau.mit.simpleftp.test;

import ru.spbau.mit.simpleftp.server.MyFtpServer;

public class Server implements Runnable {
    private MyFtpServer server;

    public Server(MyFtpServer server) {
        super();
        this.server = server;
    }

    @Override
    public void run() {
        server.start();
    }

}
