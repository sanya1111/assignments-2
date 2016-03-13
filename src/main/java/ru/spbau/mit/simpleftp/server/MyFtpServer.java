package ru.spbau.mit.simpleftp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbau.mit.simpleftp.etc.ConfigParser;

public class MyFtpServer implements Runnable {
    private static final int DEFAULT_PORT = 6666;
    private static final int THREADS_NUM_DEFAULT = 30;

    private static final ExecutorService DEFAULT_CONNECTION_EXECUTOR = Executors
            .newFixedThreadPool(THREADS_NUM_DEFAULT);

    private static final Path DEFAULT_ROOT_DIR = Paths.get("").toAbsolutePath();

    private int port;
    private Path rootDir;
    private ExecutorService connectionExecutor;

    private ServerSocket serverSocket;
    private PrintStream log;
    private BufferedReader controlInput;

    private void parseConfig(Path path) throws IOException {
        Map<String, List<String>> parsedMap = ConfigParser.parseConfig(path);
        for (Entry<String, List<String>> pair : parsedMap.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue().get(0);
            switch (key) {
            case "port":
                port = Integer.parseInt(value);
                break;
            case "root_dir":
                rootDir = Paths.get(value);
                break;
            case "threads":
                connectionExecutor = Executors.newFixedThreadPool(Integer.parseInt(value));
                break;
            default:
                break;
            }
        }
    }

    private void setupDefaults() {
        port = DEFAULT_PORT;
        connectionExecutor = DEFAULT_CONNECTION_EXECUTOR;
        rootDir = DEFAULT_ROOT_DIR;
    }

    public MyFtpServer(Path confPath, PrintStream log, InputStream controlInput) {
        this.log = log;
        this.controlInput = new BufferedReader(new InputStreamReader(controlInput));
        setupDefaults();
        if (confPath != null) {
            try {
                parseConfig(confPath);
            } catch (IOException e) {
                e.printStackTrace(log);
                setupDefaults();
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace(log);
            return;
        }
        log.println("success open server socket");

        Thread childControlThread = new Thread(this);
        childControlThread.start();

        while (true) {
            String command;
            try {
                command = controlInput.readLine();
                if (command == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace(log);
                continue;
            }

            if (command.equals("quit")) {
                break;
            }
        }

        try {
            serverSocket.close();
            log.println("success closed server socket");
            childControlThread.join();
        } catch (IOException e) {
            log.println("failed close socket, now kill child using stop");
            childControlThread.interrupt();
        } catch (InterruptedException e) {
            log.println("failed join child, now kill child using stop");
            childControlThread.interrupt();
            connectionExecutor.shutdownNow();
        }

        log.println("OK");
    }

    @Override
    public void run() {
        log.println("Child control thread is running now");
        while (!serverSocket.isClosed()) {
            Socket s;
            try {
                s = serverSocket.accept();
                connectionExecutor.execute(new MyFtpHander(s, log, rootDir));
            } catch (IOException e) {
                e.printStackTrace(log);
                break;
            }
        }
        connectionExecutor.shutdownNow();
        log.println("Child control thread have quited");
    }
}
