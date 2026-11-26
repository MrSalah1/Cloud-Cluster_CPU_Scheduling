import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMainMenu();
            int choice = readInt(scanner, "Choice: ");

            if (choice == 0) {
                break;
            }

            TestCase testCase;
            if (choice == 1) {
                testCase = readManualTestCase(scanner);
            } else if (choice == 2) {
                testCase = TestCase.testCase1();
            } else {
                testCase = TestCase.testCase2();
            }

            testCase.printHeader();
            testCase.printInputTable();

            runAlgorithmMenu(scanner, testCase);
        }

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\nSelect a test case:");
        System.out.println("1. Enter a manual test case");
        System.out.println("2. Run predefined Test Case 1 (Q=3, CS=0)");
        System.out.println("3. Run predefined Test Case 2 (Q=3, CS=1)");
        System.out.println("0. Exit");
    }

    private static void printAlgorithmMenu() {
        System.out.println("\nSelect scheduling algorithm:");
        System.out.println("1. Preemptive SJF (SRTF)");
        System.out.println("2. Adaptive Container Scheduler (ACS)");
        System.out.println("3. Both");
        System.out.println("0. Back to test case selection");
    }

    private static void runAlgorithmMenu(Scanner scanner, TestCase testCase) {
        while (true) {
            printAlgorithmMenu();
            int choice = readInt(scanner, "Choice: ");

            if (choice == 0) {
                return;
            } else if (choice == 1) {
                runSJF(testCase);
            } else if (choice == 2) {
                runACS(testCase);
            } else {
                List<Process> sjfProcesses = runSJF(testCase);
                List<Process> acsProcesses = runACS(testCase);
                printSummary(sjfProcesses, acsProcesses);
            }
        }
    }

    public static List<Process> runSJF(TestCase testCase) {
        List<Process> processes = testCase.createProcesses();

        System.out.println("\n--- SJF (SRTF) Execution Trace ---");

        SJF.run(processes, testCase.getContextSwitch());

        return processes;
    }

    public static List<Process> runACS(TestCase testCase) {
        List<Process> processes = testCase.createProcesses();

        System.out.println("\n--- ACS Execution Trace and Demotion Matrix ---");

        ACS acs = new ACS();

        acs.run(processes, testCase.getQuantum(), testCase.getContextSwitch());

        acs.printTrace();

        Results.printTable("ACS Per-Process Results", processes);

        return processes;
    }

    public static void printSummary(List<Process> sjfProcesses, List<Process> acsProcesses) {

        System.out.println("\n--- Average Metrics Summary ---");

        System.out.printf("%-26s %10s %10s%n", "Metric", "SJF", "ACS");

        System.out.printf(
                "%-26s %10.2f %10.2f%n", "Average Waiting Time",

                Results.averageWaitingTime(sjfProcesses),

                Results.averageWaitingTime(acsProcesses));

        System.out.printf("%-26s %10.2f %10.2f%n", "Average Turnaround Time",

                Results.averageTurnaroundTime(sjfProcesses),

                Results.averageTurnaroundTime(acsProcesses));
    }

    private static TestCase readManualTestCase(Scanner scanner) {
        int count = readInt(scanner, "\nNumber of processes: ");

        List<TestCase.ProcessData> data = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            System.out.println("\nEnter details for Process #" + i + ":");
            int id = readInt(scanner, "  Process ID: ");
            int arrival = readInt(scanner, "  Arrival Time: ");
            int burst = readInt(scanner, "  Burst Time: ");
            int priority = readInt(scanner, "  Priority: ");
            data.add(new TestCase.ProcessData(id, arrival, burst, priority));
        }

        int contextSwitch = readInt(scanner, "\nContext Switching Time: ");
        int quantum = readInt(scanner, "Round Robin Quantum: ");

        return new TestCase("Manual Test Case", data, quantum, contextSwitch);
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }
}
