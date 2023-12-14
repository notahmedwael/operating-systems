import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Scheduling {


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of processes: ");
        int noOfProcesses = scanner.nextInt();

        /*
        System.out.println("Enter time quantum for Round Robin Scheduler: ");
        int rrTimeQuantum = scanner.nextInt();
        System.out.println("Enter context switching time: ");
        int contextSwitchingTime = scanner.nextInt();
        */

        List<Process> processes = new ArrayList<>();

        for(int i = 0; i < noOfProcesses; i++){
            System.out.println("Enter name for process no: " + (i + 1));
            String name = scanner.next();
            System.out.println("Enter color for process no: " + (i + 1));
            String color = scanner.next();
            System.out.println("Enter arrival time for process no: " + (i + 1));
            int arrivalTime = scanner.nextInt();
            System.out.println("Enter burst time for process no: " + (i + 1));
            int burstTime = scanner.nextInt();
            System.out.println("Enter priority number for the process: " + (i + 1));
            int priorityNo = scanner.nextInt();
            Process process = new Process(name, color, arrivalTime, burstTime, priorityNo);
            processes.add(process);
        }

        SchedulerSimulator.shortestRemainingTimeFirstScheduler(processes);
//        SchedulerSimulator.priorityScheduler(processes);

        scanner.close();
    }
}