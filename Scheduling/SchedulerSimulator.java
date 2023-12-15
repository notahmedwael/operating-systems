import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class SchedulerSimulator {

    private static int getCompletionTime(Process process) {
        return process.getArrivalTime() + process.getBurstTime();
    }

    public static void shortestRemainingTimeFirstScheduler(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);
        processList.sort(Comparator.comparingInt(Process::getArrivalTime)); // Sort processes by arrival time

        int currentTime = 0;
        String currentExecutingProcess = "";
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int agingThreshold = 3;
        Set<String> completedProcesses = new HashSet<>();

        // Iterate through the processes
        while (!processList.isEmpty()) {
            // Find the processes that have arrived by the current time
            List<Process> arrivedProcesses = new ArrayList<>();
            for (Process process : processList) {
                if (process.getArrivalTime() <= currentTime) {
                    arrivedProcesses.add(process);
                }
            }

            // Sort the arrived processes by remaining burst time
            arrivedProcesses.sort(Comparator.comparingInt(Process::getBurstTime));

            // Get the process with the shortest remaining burst time
            if (!arrivedProcesses.isEmpty()) {
                Process currentProcess = arrivedProcesses.get(0);

                // Check if the current process has arrived
                if (currentTime < currentProcess.getArrivalTime()) {
                    currentTime = currentProcess.getArrivalTime();
                }

                // Aging mechanism -> Boost priority of waiting processes over time
                if (currentTime % agingThreshold == 0) {
                    for (Process waitingProcess : processList) {
                        if (!waitingProcess.getName().equals(currentProcess.getName())) {
                            waitingProcess.setBurstTime(waitingProcess.getBurstTime() + 1);
                        }
                    }
                }

                // Process the current process
                int waitingTime = currentTime - currentProcess.getArrivalTime();
                int turnaroundTime = waitingTime + currentProcess.getBurstTime();

                // Print the execution order only if the upcoming process differs from the current one
                if (!currentProcess.getName().equals(currentExecutingProcess)) {
                    System.out.println(currentProcess.getName() +
                            " -> Color: (" + currentProcess.getColor() +
                            ") -> Waiting Time: (" + waitingTime +
                            ") -> Turnaround Time: (" + turnaroundTime + ")");

                    // Update total waiting time and total turnaround time
                    totalWaitingTime += waitingTime;

                    // Add the turnaround time only if the process hasn't been completed before
                    if (!completedProcesses.contains(currentProcess.getName())) {
                        totalTurnaroundTime += turnaroundTime;
                        completedProcesses.add(currentProcess.getName());
                    }
                }

                // Subtract the time that has passed from the burst time
                currentProcess.setBurstTime(currentProcess.getBurstTime() - 1);

                // Update currentTime based on the burst time of the current process
                currentTime++;

                // Remove the processed process from the list
                if (currentProcess.getBurstTime() == 0) {
                    processList.remove(currentProcess);

                    // Set completion time for the completed process
                    currentProcess.setCompletionTime(getCompletionTime(currentProcess));
                    currentExecutingProcess = "";  // Reset the current executing process
                } else {
                    currentExecutingProcess = currentProcess.getName();
                }
            } else {
                // If no processes have arrived, move to the next time unit
                currentTime++;
            }
        }

        // Calculate and print average waiting time and average turnaround time
        int numberOfProcesses = processes.size();
        double averageWaitingTime = (double) totalWaitingTime / numberOfProcesses;
        double averageTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();

        System.out.println("\nAverage Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }

    public static void priorityScheduler(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);
        processList.sort(Comparator.comparingInt(Process::getPriorityNumber)); // Sort processes by priority

        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int agingThreshold = 3;
        List<String> executionOrder = new ArrayList<>();

        // Iterate through the processes
        while (!processList.isEmpty()) {
            Process currentProcess = processList.get(0);

            // Aging mechanism -> Boost priority of waiting processes over time
            if (currentTime % agingThreshold == 0) {
                for (Process waitingProcess : processList) {
                    if (!waitingProcess.getName().equals(currentProcess.getName())) {
                        waitingProcess.setPriorityNumber(waitingProcess.getPriorityNumber() + 1);
                    }
                }
            }

            // Process the current process
            int waitingTime = currentTime;
            int turnaroundTime = waitingTime + currentProcess.getBurstTime();

            // Record the execution order
            executionOrder.add(currentProcess.getName() +
                    " -> Color: (" + currentProcess.getColor() +
                    ") -> Waiting Time: (" + waitingTime +
                    ") -> Turnaround Time: (" + turnaroundTime + ")");

            // Update total waiting time and total turnaround time
            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            // Update currentTime based on the burst time of the current process
            currentTime += currentProcess.getBurstTime();

            // Remove the processed process from the list
            processList.remove(currentProcess);

            // Set completion time for the completed process
            currentProcess.setCompletionTime(getCompletionTime(currentProcess));
        }

        // Print the execution order
        System.out.println("\nExecution Order:");
        for (String order : executionOrder) {
            System.out.println(order);
        }

        // Calculate and print average waiting time and average turnaround time
        int numberOfProcesses = processes.size();
        double averageWaitingTime = (double) totalWaitingTime / numberOfProcesses;
        double averageTurnaroundTime = (double) totalTurnaroundTime / numberOfProcesses;

        System.out.println("\nAverage Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }
}
