import java.util.*;

public class ACS {

    private final RoundRobin tier1;
    private final PriorityScheduling tier2Scheduler;
    private final List<Process> tier2;

    public ACS() {
        tier2 = new ArrayList<>();

        tier1 = new RoundRobin(tier2);
        tier2Scheduler = new PriorityScheduling();
    }

    public void run(
            List<Process> processes,
            int quantum,
            int contextSwitching) {

        tier2.clear();

        int currentTime = tier1.run(
                processes,
                quantum,
                contextSwitching
        );

        tier2Scheduler.run(tier2, currentTime);
    }
}