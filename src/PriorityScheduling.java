
import java.util.*;

public class PriorityScheduling {

    public void run(List<Process> tier2, int currentTime) {

        while (!tier2.isEmpty()) {

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
                highestPriorityProcess.executeOneUnit();
                currentTime++;
            }

            // Record completion time
            highestPriorityProcess.recordFinish(currentTime);

            System.out.println(
                    "P" + highestPriorityProcess.getId()
                    + " finished at time " + currentTime
            );
        }
    }
}
