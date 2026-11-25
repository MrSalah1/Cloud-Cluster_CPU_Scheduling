import java.util.*;

public class RoundRobin {
    private final Queue<Process> readyQ = new LinkedList<>();
    private final List<Process> tier2;

    public RoundRobin(List<Process> tier2) {
        this.tier2 = tier2;
    }

    public int run(List<Process> processes, int quantum, int contextSwitching) {
        if (processes.isEmpty()) {
            return 0;
        }

        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currtime = 0;
        int handled = 0;
        int n = sortedProcesses.size();
        int nextProcess = 0;

        while(handled < n) {
            while(nextProcess < n && sortedProcesses.get(nextProcess).getArrivalTime() <= currtime) {
                readyQ.add(sortedProcesses.get(nextProcess));
                nextProcess++;
            }

            if (readyQ.isEmpty()) {
                if (nextProcess >= n) {
                    break;
                }
                currtime = sortedProcesses.get(nextProcess).getArrivalTime();
                continue;
            }

            Process current =readyQ.poll();
            current.recordFirstStart(currtime);

            int executionTime = Math.min(quantum, current.getRemainingTime());

            for (int i=0; i<executionTime; i++) {
                current.executeOneUnit();
                currtime++;
            }

            while (nextProcess < n && sortedProcesses.get(nextProcess).getArrivalTime() <= currtime) {
                readyQ.add(sortedProcesses.get(nextProcess));
                nextProcess++;
            }

            if (current.isFinished()) {
                current.recordFinish(currtime);
            } else {
                // Record the time that the process finished in tier 1 
                current.recordTier2Entry(currtime);
                tier2.add(current);
            }
            handled++;

            if(handled < n && !readyQ.isEmpty()) {
                currtime+= contextSwitching;
            }
        }

        // return current time to use it in tier 2
        return currtime;
    }
}