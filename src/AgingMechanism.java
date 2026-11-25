import java.util.List;

public class AgingMechanism {
    public static final int AGING_INTERVAL = 4;

    public int run(List<Process> tier2, int currentTime, int contextSwitching) {
        boolean hasPreviousProcess = false;

        while (!tier2.isEmpty()) {
            Process selected = selectLowestEffectivePriority(tier2, currentTime);
            tier2.remove(selected);

            if (hasPreviousProcess) {
                currentTime += contextSwitching;
            }

            hasPreviousProcess = true;

            while (!selected.isFinished()) {
                selected.executeOneUnit();
                currentTime++;
            }

            selected.recordFinish(currentTime);
        }

        return currentTime;
    }

    private Process selectLowestEffectivePriority(
            List<Process> tier2,
            int currentTime) {

        Process selected = tier2.get(0);

        for (int i = 1; i < tier2.size(); i++) {
            Process candidate = tier2.get(i);
            int candidatePriority =
                    getEffectivePriority(candidate, currentTime);
            int selectedPriority =
                    getEffectivePriority(selected, currentTime);

            if (candidatePriority < selectedPriority
                    || (candidatePriority == selectedPriority
                        && candidate.getTier2EntryTime()
                            < selected.getTier2EntryTime())
                    || (candidatePriority == selectedPriority
                        && candidate.getTier2EntryTime()
                            == selected.getTier2EntryTime()
                        && candidate.getId() < selected.getId())) {
                selected = candidate;
            }
        }

        return selected;
    }

    public static int getEffectivePriority(Process process, int currentTime) {
        int waitingTime = currentTime - process.getTier2EntryTime();
        int agingSteps = waitingTime / AGING_INTERVAL;
        return process.getPriority() - agingSteps;
    }
}
