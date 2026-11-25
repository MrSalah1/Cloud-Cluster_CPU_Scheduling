import java.util.List;

public class SJF {
    public static void run(List<Process> processes, int contextSwitchTime) {
        if (processes.isEmpty()) {
            return;
        }

        int n = processes.size();
        int time = 0;
        int completed = 0;
        int switchRemaining = 0;
        boolean needSwitch = false;
        Process running = null;

        System.out.println("Execution Timeline:");
        System.out.println("Time\tActivity");

        while (completed < n) {
            if (switchRemaining > 0) {
                System.out.println(time + "\tCS");
                switchRemaining--;
                time++;
                continue;
            }

            Process best = pickShortest(processes, time, running);

            if (best == null) {
                System.out.println(time + "\tIDLE");
                time++;
                running = null;
                needSwitch = false;
                continue;
            }

            if (running == null) {
                if (needSwitch && contextSwitchTime > 0) {
                    switchRemaining = contextSwitchTime;
                    needSwitch = false;
                    continue;
                }
                running = best;
                running.recordFirstStart(time);
                needSwitch = false;
            } else if (best != running) {
                running = null;
                needSwitch = false;//redunant but keep for readabilty
                if (contextSwitchTime > 0) {
                    switchRemaining = contextSwitchTime;
                    continue;
                }
                running = best;
                running.recordFirstStart(time);
            }

            System.out.println(time + "\tP" + running.getId());
            running.executeOneUnit();
            time++;

            if (running.isFinished()) {
                running.recordFinish(time);
                completed++;
                running = null;
                needSwitch = true;
            }
        }

        printMetrics(processes);
    }

    private static Process pickShortest(List<Process> processes, int time, Process running) {
        Process best = null;

        for (Process p : processes) {
            if (p.getArrivalTime() <= time && !p.isFinished()) {
                if (best == null
                        || p.getRemainingTime() < best.getRemainingTime()
                        || (p.getRemainingTime() == best.getRemainingTime()
                            && p.getArrivalTime() < best.getArrivalTime())) {
                    best = p;
                }
            }
        }

        if (best != null
                && running != null
                && !running.isFinished()
                && running.getRemainingTime() == best.getRemainingTime()) {
            return running;
        }

        return best;
    }

    private static void printMetrics(List<Process> processes) {
        Results.printTable("SJF Per-Process Results", processes);
    }
}
