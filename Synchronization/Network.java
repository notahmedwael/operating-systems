import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Semaphore class for handling synchronization
class Semaphore {
    private int value;             // Semaphore value
    private final FileWriter fileWriter;  // FileWriter to log semaphore actions

    // Constructor for Semaphore
    public Semaphore(int value, FileWriter fileWriter) {
        this.value = value;
        this.fileWriter = fileWriter;
    }

    // Method for a thread to wait on the semaphore
    public synchronized void wait(String device) throws IOException {
        value--;
        if (value < 0) {
            try {
                // Log the arrival of a device and that it's waiting
                fileWriter.write("- " + device + " arrived and waiting\n");
                wait();  // Wait until signaled by another thread
            } catch (InterruptedException ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        } else {
            // Log the arrival of a device
            fileWriter.write("- " + device + " arrived\n");
            notify();  // Notify waiting threads to start working
        }
    }

    // Method to signal the semaphore
    public synchronized void signal() {
        value++;
        if (value <= 0) {
            notify();  // Notify waiting threads to start working
        }
    }
}

// Router class to manage connections
class Router {
    private final Semaphore semaphore;        // Semaphore to control access to connections
    private int connectionNumber = 0;         // New connection number
    private final String[] occupiedDevices;   // To store the currently occupied devices for each connection
    private final List<String> logs;           // To store logs of connections

    // Constructor for Router
    public Router(int maxConnections, Semaphore semaphore, List<String> logs) {
        this.semaphore = semaphore;
        this.occupiedDevices = new String[maxConnections];
        this.logs = logs;
    }

    // Method for a device to occupy a connection
    public void occupyConnection(String device) throws InterruptedException, IOException {
        semaphore.wait(device);

        // Find the first available connection
        for (int i = 0; i < occupiedDevices.length; i++) {
            if (occupiedDevices[i] == null) {
                occupiedDevices[i] = device;
                connectionNumber++;  // Increment connection number for each new connection
                logs.add("- Connection " + connectionNumber + ": " + device + " Occupied");
                logs.add("- Connection " + connectionNumber + ": " + device + " login");
                return;
            }
        }
    }

    // Method for a device to release a connection
    public void releaseConnection(String device) {
        for (int i = 0; i < occupiedDevices.length; i++) {
            if (device.equals(occupiedDevices[i])) {
                occupiedDevices[i] = null;
                semaphore.signal();
                return;
            }
        }
    }

    // Method to get the connection number for a device
    public int getConnectionNumber(String device) {
        for (int i = 0; i < occupiedDevices.length; i++) {
            if (device.equals(occupiedDevices[i])) {
                return i + 1;  // Connection number is 1-based
            }
        }
        return -1;  // The Device is not connected
    }
}

// Device class representing a connected device
class Device extends Thread {
    private final String name;
    private final String type;
    private final Router router;
    private final List<String> logs;

    // Constructor for Device
    public Device(String name, String type, Router router, List<String> logs) {
        this.name = name;
        this.type = type;
        this.router = router;
        this.logs = logs;
        start();  // Start the thread immediately upon creation
    }

    // Run method for the thread
    @Override
    public void run() {
        try {
            router.occupyConnection("(" + name + ")(" + type + ")");

            // Simulate online activity
            Thread.sleep((long) (Math.random() * 10000) + 5000);  // Vary between 5 and 15 seconds
            logs.add("- Connection " + router.getConnectionNumber("(" + name + ")(" + type + ")") + ": " + name + " performs online activity");

            // Simulate logout
            Thread.sleep((long) (Math.random() * 10000) + 5000);  // Vary between 5 and 15 seconds
            logs.add("- Connection " + router.getConnectionNumber("(" + name + ")(" + type + ")") + ": " + name + " Logged out");

        } catch (InterruptedException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            Thread.currentThread().interrupt();  // Restore interrupted state
        } finally {
            router.releaseConnection("(" + name + ")(" + type + ")");
        }
    }
}

// Main class for the network simulation
public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character

        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("output.txt");
            Semaphore semaphore = new Semaphore(maxConnections, fileWriter);
            List<String> logs = Collections.synchronizedList(new ArrayList<>());
            Router router = new Router(maxConnections, semaphore, logs);

            List<Device> devices = new ArrayList<>();
            for (int i = 1; i <= totalDevices; i++) {
                System.out.println("Enter the name and type of device " + i + " (separated by a space):");
                String[] input = scanner.nextLine().split(" ");
                String name = input[0];
                String type = input.length > 1 ? input[1] : "";
                devices.add(new Device(name, type, router, logs));
            }

            // Wait for all threads to finish
            for (Device device : devices) {
                device.join();
            }

            for (String log : logs) {
                fileWriter.write(log + "\n");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.err.println("Error closing FileWriter: " + e.getMessage());
                }
            }
        }
    }
}
