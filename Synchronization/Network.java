import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

// Utility class for writing log messages to a file
class FileUtils {
    // Method to write a formatted log message to the specified file
    public static void writeToLogFile(String message, Object... args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt", true))) {
            writer.printf(message + "%n", args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

// Router class manages the connections and waiting clients
class Router {
    private final Semaphore semaphore;
    private final Queue<Device> waitingClients = new LinkedList<>();

    // Constructor initializes the router with a maximum number of connections
    Router(int maxConnections) {
        semaphore = new Semaphore(maxConnections);
    }

    // Method for a device to connect to the router
    public int connect(Device device) {
        int connectionNum = semaphore.acquire();
        if (connectionNum != 0) {
            // Log the connection login
            FileUtils.writeToLogFile("Connection %d: %s(%s) login", connectionNum, device.name, device.type);
        } else {
            // Log that a device has arrived and is waiting
            FileUtils.writeToLogFile("(%s)(%s) arrived", device.name, device.type);
            waitingClients.add(device);
            FileUtils.writeToLogFile("(%s)(%s) arrived and waiting", device.name, device.type);
        }
        return connectionNum;
    }

    // Method for releasing a connection when a device logs out
    public void release() {
        semaphore.release();
        notifyNextWaitingClient();
    }

    // Private method to notify the next waiting client
    private synchronized void notifyNextWaitingClient() {
        Device waitingClient = waitingClients.poll();
        if (waitingClient != null) {
            waitingClient.start();
        }
    }

    // Public method to get the next waiting client
    public synchronized Device getNextWaitingClient() {
        return waitingClients.poll();
    }
}

// Semaphore class manages the number of available connections
class Semaphore {
    private final int maxConnections;
    private int n;

    // Constructor initializes the semaphore with a maximum number of connections
    Semaphore(int maxConnections) {
        this.maxConnections = maxConnections;
        n = 0;
    }

    // Method for a device to acquire a connection
    public synchronized int acquire() {
        while (n >= maxConnections) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        n++;
        return n;
    }

    // Method for a device to release a connection
    public synchronized void release() {
        n--;
        notify();
    }
}

// Device class represents a client device
class Device extends Thread {
    String name;
    String type;
    Router router;
    int connectionNum;

    // Constructor initializes a device with a name, type, and router
    public Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    // Run method executed when a device is started
    @Override
    public void run() {
        int connectionNumber = router.connect(this);
        if (connectionNumber != 0) {
            // Set the connectionNum to the actual connection number
            connectionNum = connectionNumber;

            performOnlineActivity();
            router.release();
            // Log the connection logout
            FileUtils.writeToLogFile("Connection %d: %s(%s) Logged out", connectionNumber, name, type);

            // Check if there are waiting clients
            Device waitingClient = router.getNextWaitingClient();
            if (waitingClient != null) {
                waitingClient.start();
            }
        }
    }

    // Simulate online activity with a random sleep time
    private void performOnlineActivity() {
        int randomSleep = 5000 + (int) (Math.random() * 10000); // Random sleep between 5 and 15 seconds
        FileUtils.writeToLogFile("Connection %d: %s(%s) performs online activity", connectionNum, name, type);
        try {
            Thread.sleep(randomSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

// The main class representing the network simulation
public class Network {
    // Main method to start the network simulation
    public static void main(String[] args) {
        int connections, clients;
        Queue<Device> devices = new LinkedList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        connections = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        clients = scanner.nextInt();
        Router router = new Router(connections);

        for (int i = 0; i < clients; i++) {
            System.out.println("Enter the name and type of device (e.g., C1 mobile):");
            String name = scanner.next();
            String type = scanner.next();
            Device device = new Device(name, type, router);
            devices.add(device);
        }

        while (!devices.isEmpty()) {
            Device device = devices.poll();
            device.start();
        }

        scanner.close();
    }
}