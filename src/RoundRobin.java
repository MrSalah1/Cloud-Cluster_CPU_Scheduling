import java.util.*;

public class RoundRobin {
    private final Queue<Process> readyQ = new LinkedList<>();
    private final List<Process> tier2;
    private final ACSTrace trace;

    public RoundRobin(List<Process> tier2, ACSTrace trace) {
        this.tier2 = tier2;
        this.trace = trace;
    }

    public int run(List<Process> processes, int quantum, int contextSwitching) {
        readyQ.clear();

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
                int nextArrival = sortedProcesses.get(nextProcess).getArrivalTime();
                while (currtime < nextArrival) {
                    trace.add(currtime, "IDLE", readyQ, tier2);
                    currtime++;
                }
                continue;
            }

            Process current =readyQ.poll();
            current.recordFirstStart(currtime);

            int executionTime = Math.min(quantum, current.getRemainingTime());

            for (int i=0; i<executionTime; i++) {
                trace.add(currtime,
                        "P" + current.getId() + " (rem " + current.getRemainingTime() + ")",
                        readyQ, tier2);
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
                trace.demote("P" + current.getId() + " (quantum expired)");
            }
            handled++;

            if(handled < n && !readyQ.isEmpty()) {
                for (int cs = 0; cs < contextSwitching; cs++) {
                    trace.add(currtime, "CS", readyQ, tier2);
                    currtime++;
                }
            }
        }

        // return current time to use it in tier 2
        return currtime;
    }
}
