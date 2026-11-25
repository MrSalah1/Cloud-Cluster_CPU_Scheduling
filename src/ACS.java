import java.util.*;

public class ACS {

    private final RoundRobin tier1;
    private final AgingMechanism tier2Scheduler;
    private final List<Process> tier2;
    private final ACSTrace trace;

    public ACS() {
        tier2 = new ArrayList<>();
        trace = new ACSTrace();
        tier1 = new RoundRobin(tier2, trace);
        tier2Scheduler = new AgingMechanism(trace);
    }

    public void run(
            List<Process> processes,
            int quantum,
            int contextSwitching) {

        tier2.clear();
        trace.clear();

        int currentTime = tier1.run(
                processes,
                quantum,
                contextSwitching
        );

        tier2Scheduler.run(tier2, currentTime, contextSwitching);
    }

    public void printTrace() {
        trace.print();
    }
}
