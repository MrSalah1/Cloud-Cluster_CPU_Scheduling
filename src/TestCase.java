import java.util.ArrayList;
import java.util.List;

public class TestCase {
    public static class ProcessData {
        public final int id;
        public final int arrivalTime;
        public final int burstTime;
        public final int priority;

        public ProcessData(int id, int arrivalTime, int burstTime, int priority) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }

    private final String name;
    private final List<ProcessData> processes;
    private final int quantum;
    private final int contextSwitch;

    public TestCase(String name, List<ProcessData> processes, int quantum, int contextSwitch) {
        this.name = name;
        this.processes = new ArrayList<>(processes);
        this.quantum = quantum;
        this.contextSwitch = contextSwitch;
    }

    public static TestCase testCase1() {
        List<ProcessData> data = new ArrayList<>();
        data.add(new ProcessData(1, 0, 4, 2));
        data.add(new ProcessData(2, 1, 7, 3));
        data.add(new ProcessData(3, 3, 2, 1));
        return new TestCase("Test Case 1", data, 3, 0);
    }

    public static TestCase testCase2() {
        List<ProcessData> data = new ArrayList<>();
        data.add(new ProcessData(1, 0, 5, 4));
        data.add(new ProcessData(2, 1, 6, 2));
        data.add(new ProcessData(3, 4, 4, 3));
        return new TestCase("Test Case 2", data, 3, 1);
    }

    public List<Process> createProcesses() {
        List<Process> result = new ArrayList<>();
        for (ProcessData p : processes) {
            result.add(new Process(p.id, p.arrivalTime, p.burstTime, p.priority));
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getContextSwitch() {
        return contextSwitch;
    }

    public void printHeader() {
        System.out.println("============================================================");
        System.out.println(" Test Case: " + name);
        System.out.printf(" Scheduling Parameters: Quantum = %d, Context Switching = %d%n",
                quantum, contextSwitch);
        System.out.println("============================================================");
    }

    public void printInputTable() {
        System.out.println("\nInput Process Table:");
        System.out.printf("%-8s %8s %6s %8s%n", "Process", "Arrival", "Burst", "Priority");
        for (ProcessData p : processes) {
            System.out.printf("%-8s %8d %6d %8d%n",
                    "P" + p.id, p.arrivalTime, p.burstTime, p.priority);
        }
    }
}
