import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Terminal {
    private final Parser parser;
    Path currentDirectory = Paths.get(""); //   Initializing path
    private final List<String> commandHistory; //   List of commands history

    //  Constructor to construct a parser for the terminal and a list of command history
    public Terminal() {
        parser = new Parser();
        commandHistory = new ArrayList<>();
    }

    //  A method that returns whatever string sent to it
    public void echo(String[] args) {
        if (args.length > 0) {
            System.out.println(String.join(" ", args)); //  Returns a string joined on whitespace
        } else {
            System.out.println("Please provide text to echo.");
        }
    }

    //  A method that returns current directory using Path class
    public void pwd() {
        System.out.println("Current directory: " + currentDirectory.toAbsolutePath());
    }

    //  A method that returns current history of commands executed by user
    public void history() {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    //  A method that returns both a list of current directory files in order and in reverse order based on input
    //  ls => in order | ls -r in reverse order
    public void ls(boolean reverseOrder) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            List<Path> entries = new ArrayList<>();
            for (Path entry : directoryStream) {
                entries.add(entry);
            }

            if (reverseOrder) {
                Collections.reverse(entries);
            }

            for (Path entry : entries) {
                System.out.println(entry.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error listing directory: " + e.getMessage());
        }
    }

    //  A method that deals with the command entered by user and calls appropriate method for the command to be executed
    public void chooseCommandAction() {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        commandHistory.add(commandName + " " + String.join(" ", args)); //  Add the command to history

        switch (commandName) {
            case "echo":
                echo(args);
                break;
            case "pwd":
                pwd();
                break;
            case "history":
                history();
                break;
            case "ls":
                ls(false);
                break;
            case "ls -r":
                ls(true);
                break;
            case "exit":
                System.out.println("Exiting the program.");
                System.exit(0);
                break;
            default:
                System.out.println("Command not recognized.");
        }
    }

    //  Driver code to start the program
    public static void main(String[] args) {
        Terminal terminal = new Terminal(); //  Initialize a terminal that will use parser to parse input and deal with commands
        Scanner scanner = new Scanner(System.in);   //  Get Input from user

        //  An infinite loop that exits on user entering "exit"
        while (true){
            System.out.println("Enter command: ");
            String input = scanner.nextLine();
            if (terminal.parser.parse(input)){
                terminal.chooseCommandAction(); //  Call appropriate method to deal with the command
            }else {
                System.out.println("Invalid input, please enter a valid command");  //  Tell user to enter a valid command
            }
            }
        }
    }