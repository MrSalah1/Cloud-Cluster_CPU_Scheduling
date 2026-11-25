import java.util.Collections;
import java.util.List;

public class AgingMechanism {
    public static final int AGING_INTERVAL = 4;
    private final PriorityScheduling priorityScheduling;
    private final ACSTrace trace;

    public AgingMechanism(ACSTrace trace) {
        this.trace = trace;
        this.priorityScheduling = new PriorityScheduling(trace);
    }

    public int run(List<Process> tier2, int currentTime, int contextSwitching) {
        while (!tier2.isEmpty()) {
            updatePriorities(tier2, currentTime);

            for (int cs = 0; cs < contextSwitching; cs++) {
                trace.add(currentTime, "CS", Collections.emptyList(), tier2);
                currentTime++;
            }

            currentTime = priorityScheduling.run(tier2, currentTime);
        }

        return currentTime;
    }

    private void updatePriorities(List<Process> tier2, int currentTime) {
        for (Process process : tier2) {
            process.setPriority(getEffectivePriority(process, currentTime));
        }
    }

    public static int getEffectivePriority(Process process, int currentTime) {
        int waitingTime = Math.max(0, currentTime - process.getTier2EntryTime());
        int agingSteps = waitingTime / AGING_INTERVAL;
        return process.getInitialPriority() - agingSteps;
    }
}
