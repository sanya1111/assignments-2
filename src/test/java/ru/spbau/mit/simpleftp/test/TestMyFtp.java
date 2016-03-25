package ru.spbau.mit.simpleftp.test;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import ru.spbau.mit.simpleftp.common.MyFtpRequest;
import ru.spbau.mit.simpleftp.server.MyFtpServer;

public class TestMyFtp {
    private static final Path RES_DIR = Paths.get("src/test/resources");
    private static final Path CONFIG_DIR = RES_DIR.resolve("conf");
    private static final Path CLIENT_CONF = CONFIG_DIR.resolve("client.conf");
    private static final Path DB_CONF = CONFIG_DIR.resolve("db.conf");

    private static final Path FTP_FILES = RES_DIR.resolve("ftp_files");
    private static final Path DOWNLOAD_FILES = RES_DIR.resolve("download_files");

    private static final Path LIST_CLIENT_REQUEST = Paths.get("");
    private static final Path LIST_CLIENT_FAIL_REQUEST = Paths.get("NOPE");
    private static final Path GET_CLIENT_REQUEST = Paths.get("pushkin/serv");
    private static final Path GET_CLIENT_DOWNLOAD_PATH = DOWNLOAD_FILES.resolve(Paths.get("newserv"));
    private static final Path GET_CLIENT_FAIL_REQUEST = DOWNLOAD_FILES.resolve("NOPE");

    private static final int SERVER_INIT_WAIT_TIME = 5000;

    private static GetClient newGetClient() {
        return new GetClient(CLIENT_CONF, System.out, FTP_FILES.resolve(GET_CLIENT_REQUEST),
                GET_CLIENT_DOWNLOAD_PATH, new MyFtpRequest(MyFtpRequest.Type.GET, GET_CLIENT_REQUEST));
    }

    private static GetClientFail newGetClientFail() {
        return new GetClientFail(CLIENT_CONF, System.out, GET_CLIENT_DOWNLOAD_PATH,
                new MyFtpRequest(MyFtpRequest.Type.GET, GET_CLIENT_FAIL_REQUEST));

    }

    private static ListClient newListClient() {
        return new ListClient(CLIENT_CONF, System.out, FTP_FILES,
                new MyFtpRequest(MyFtpRequest.Type.LIST, LIST_CLIENT_REQUEST));
    }

    private static ListClientFail newListClientFail() {
        return new ListClientFail(CLIENT_CONF, System.out,
                new MyFtpRequest(MyFtpRequest.Type.LIST, LIST_CLIENT_FAIL_REQUEST));

    }

    private static MyFtpServer newServer() {
        return new MyFtpServer(DB_CONF, new ServerLogStream(System.err), System.in);
    }

    private static void doJoin(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMyFtpWithConnection() {
        Thread serverThread = new Thread(new Server(newServer()));
        serverThread.start();
        /*
         * i don't want to synchronize server with client - we just wait for a
         * long, reconnect if failed with connect for some times and wait again
         */
        try {
            Thread.sleep(SERVER_INIT_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListClient testListClient = newListClient();
        ListClientFail testListClientFail = newListClientFail();
        GetClient testGetClient = newGetClient();
        GetClientFail testGetClientFail = newGetClientFail();

        Thread clientListThread = new Thread(testListClient);
        Thread clientListFailThread = new Thread(testListClientFail);
        Thread clientGetThread = new Thread(testGetClient);
        Thread clientGetFailThread = new Thread(testGetClientFail);

        clientListThread.start();
        clientListFailThread.start();
        clientGetThread.start();
        clientGetFailThread.start();

        doJoin(clientListFailThread);
        doJoin(clientListThread);
        doJoin(clientGetThread);
        doJoin(clientGetFailThread);

        assertTrue(testListClientFail.getResult());
        assertTrue(testListClient.getResult());
        assertTrue(testGetClient.getResult());
        assertTrue(testGetClientFail.getResult());
    }
}
