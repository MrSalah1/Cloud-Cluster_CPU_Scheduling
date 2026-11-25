import java.util.List;

public class Results {
    public static void printTable(String title, List<Process> processes) {
        System.out.println("\n" + title + ":");
        System.out.printf("%-8s %8s %8s %11s %8s%n",
                "Process", "Arrival", "Finish", "Turnaround", "Waiting");
        for (Process p : processes) {
            System.out.printf("%-8s %8d %8d %11d %8d%n",
                    "P" + p.getId(),
                    p.getArrivalTime(),
                    p.getFinishTime(),
                    p.getTurnaroundTime(),
                    p.getWaitingTime());
        }
        System.out.printf("Average Waiting Time:    %.2f%n", averageWaitingTime(processes));
        System.out.printf("Average Turnaround Time: %.2f%n", averageTurnaroundTime(processes));
    }

    public static double averageWaitingTime(List<Process> processes) {
        if (processes.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Process p : processes) {
            total += p.getWaitingTime();
        }
        return (double) total / processes.size();
    }

    public static double averageTurnaroundTime(List<Process> processes) {
        if (processes.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Process p : processes) {
            total += p.getTurnaroundTime();
        }
        return (double) total / processes.size();
    }
}
