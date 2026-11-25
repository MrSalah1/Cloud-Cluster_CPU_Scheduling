import java.util.List;

public class AgingMechanism {
    public static final int AGING_INTERVAL = 4;
    private final PriorityScheduling priorityScheduling;

    public AgingMechanism() {
        this.priorityScheduling = new PriorityScheduling();
    }

    public int run(List<Process> tier2, int currentTime, int contextSwitching) {
        while (!tier2.isEmpty()) {
            updatePriorities(tier2, currentTime);

            currentTime += contextSwitching;
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
