import java.util.List;

public class AgingMechanism {
    public static final int AGING_INTERVAL = 4;

    public void updatePriorities(List<Process> tier2, int currentTime) {
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
