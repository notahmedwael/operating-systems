import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

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

                // Process the current process
                int waitingTime = currentTime - currentProcess.getArrivalTime();  // Calculate waiting time
                int turnaroundTime = waitingTime + currentProcess.getBurstTime();

                // Print the execution order only if the upcoming process differs from the current one
                if (!currentProcess.getName().equals(currentExecutingProcess)) {
                    System.out.println(currentProcess.getName() +
                            " -> Color: (" + currentProcess.getColor() +
                            ") -> Waiting Time: (" + waitingTime +
                            ") -> Turnaround Time: (" + turnaroundTime + ")");

                    // Update total waiting time and total turnaround time
                    totalWaitingTime += waitingTime;
                    totalTurnaroundTime += turnaroundTime;
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
        double averageTurnaroundTime = (double) totalTurnaroundTime / numberOfProcesses;

        System.out.println("\nAverage Waiting Time: " + averageWaitingTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
    }

    public static void priorityScheduler(List<Process> processes){

    }
}