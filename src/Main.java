import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int contextSwitchTime = 1;

        while (true) {
            System.out.println("\nSelect scheduling algorithm:");
            System.out.println("1. Preemptive SJF (SRTF)");
            System.out.println("2. Adaptive Container Scheduler (ACS)");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();

            if (choice == 0) {
                break;
            } else if (choice == 1) {
                System.out.println("\nRunning Preemptive SJF (SRTF) with CS = "
                        + contextSwitchTime);
                SJF.run(createProcesses(), contextSwitchTime);
            } else if (choice == 2) {
                /*Acs*/
            } else {
                System.out.println("Error");
            }
        }

        scanner.close();
    }

    private static List<Process> createProcesses() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 0, 4, 2));
        processes.add(new Process(2, 1, 7, 3));
        processes.add(new Process(3, 3, 2, 1));
        return processes;
    }
}
