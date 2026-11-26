import java.util.*;

public class ACS {
    private final Queue<Process> tier1;
    private final List<Process> tier2;
    private final AgingMechanism agingMechanism;
    private final ACSTrace trace;

    public ACS() {
        this.tier1 = new LinkedList<>();
        this.tier2 = new ArrayList<>();
        this.trace = new ACSTrace();
        this.agingMechanism = new AgingMechanism();
    }

    public void run(List<Process> processes, int quantum, int contextSwitching) {
        tier1.clear();
        tier2.clear();
        trace.clear();

        if(processes.isEmpty()) {
            return;
        }

        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::getArrivalTime));

        int n = sorted.size();
        int nextP = 0;
        int completed = 0;
        int currentTime = 0;
        boolean last = false;

        while(completed < n) {
            nextP = enqueueArrivals(sorted, nextP, currentTime);
            // Tier 1
            if (!tier1.isEmpty()) {
                Process current = tier1.poll();

                if(last){
                    for (int cs = 0; cs < contextSwitching; cs++) {
                        nextP = enqueueArrivals(sorted, nextP, currentTime);
                        trace.add(currentTime, "CS", tier1, tier2);
                        currentTime++;
                    }
                    nextP = enqueueArrivals(sorted, nextP, currentTime);
                }

                current.recordFirstStart(currentTime);

                int executionTime = Math.min(quantum, current.getRemainingTime());
                for (int i=0; i<executionTime; i++) {
                    nextP = enqueueArrivals(sorted, nextP, currentTime);
                    trace.add(currentTime,
                            "P" + current.getId() + " (rem " + current.getRemainingTime() + ")",
                            tier1, tier2);
                    current.executeOneUnit();
                    currentTime++;
                }

                nextP = enqueueArrivals(sorted, nextP, currentTime);

                if (current.isFinished()) {
                    current.recordFinish(currentTime);
                    completed++;
                } else {
                    current.recordTier2Entry(currentTime);
                    tier2.add(current);
                    trace.demote("P" + current.getId() + " (quantum expired)");
                }

                last = true;
            }
            // Tier 2
            else if(!tier2.isEmpty()) {
                agingMechanism.updatePriorities(tier2, currentTime);
                Process current = getHighestPriority(tier2);

                if(last){
                    for (int cs=0; cs<contextSwitching; cs++) {
                        nextP = enqueueArrivals(sorted, nextP, currentTime);
                        trace.add(currentTime, "CS", tier1, tier2);
                        currentTime++;
                    }
                    nextP = enqueueArrivals(sorted, nextP, currentTime);
                }

                int executionTime =current.getRemainingTime();
                for (int i=0; i<executionTime; i++) {
                    nextP = enqueueArrivals(sorted, nextP, currentTime);
                    trace.add(currentTime,
                            "P" + current.getId()
                                    + " (rem " + current.getRemainingTime()
                                    + ", prio " + current.getPriority() + ")",
                            tier1, tier2);
                    current.executeOneUnit();
                    currentTime++;
                }

                current.recordFinish(currentTime);
                completed++;
                last = true;
            }
            // both empty
            else {
                trace.add(currentTime, "IDLE", tier1, tier2);
                currentTime++;
                last = false;
            }
        }
    }

    private int enqueueArrivals(List<Process> sorted, int nextP, int currentTime) {
        while (nextP < sorted.size() && sorted.get(nextP).getArrivalTime() <= currentTime) {
            tier1.add(sorted.get(nextP));
            nextP++;
        }
        return nextP;
    }

    public void printTrace() {
        trace.print();
    }

    public Process getHighestPriority(List<Process> tier2) {
        Process highestPriorityProcess = tier2.get(0);

        for (int i = 1; i < tier2.size(); i++) {

            Process candidate = tier2.get(i);

            if (candidate.getPriority()
                    < highestPriorityProcess.getPriority()) {

                highestPriorityProcess = candidate;
            }
        }

        // Remove the selected process from the list
        tier2.remove(highestPriorityProcess);

        return highestPriorityProcess;
    }
}