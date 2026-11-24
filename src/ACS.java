import java.util.*;

public class ACS {
    private final RoundRobin tier1;
    private final List<Process> tier2;

    public ACS() {
        tier2 = new ArrayList<>();
        tier1 = new RoundRobin(tier2);
    }

    public void run(List<Process> processes, int quantum, int contextSwitching) {
        tier1.run(processes, quantum, contextSwitching);
    }


}