
import java.util.*;

public class PriorityScheduling {
    private final ACSTrace trace;

    public PriorityScheduling(ACSTrace trace) {
        this.trace = trace;
    }

    public int run(List<Process> tier2, int currentTime) {
        if (tier2.isEmpty()) {
            return currentTime;
        }

        // Get the highest priority process
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

        // Execute the selected process
        int executionTime =
                highestPriorityProcess.getRemainingTime();

        for (int i = 0; i < executionTime; i++) {
            trace.add(currentTime,
                    "P" + highestPriorityProcess.getId()
                            + " (rem " + highestPriorityProcess.getRemainingTime()
                            + ", prio " + highestPriorityProcess.getPriority() + ")",
                    Collections.emptyList(), tier2);
            highestPriorityProcess.executeOneUnit();
            currentTime++;
        }

        // Record completion time
        highestPriorityProcess.recordFinish(currentTime);

        return currentTime;
    }
}
