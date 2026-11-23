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
        int totalTat = 0;
        int totalWt = 0;
        int totalRt = 0;

        System.out.println("\nProcess Metrics:");
        System.out.printf("%-8s %-4s %-4s %-6s %-7s %-4s %-4s %-4s%n",
                "Process", "AT", "BT", "Start", "Finish", "TAT", "WT", "RT");

        for (Process p : processes) {
            int tat = p.getTurnaroundTime();
            int wt = p.getWaitingTime();
            int rt = p.getResponseTime();

            totalTat += tat;
            totalWt += wt;
            totalRt += rt;

            System.out.printf("%-8s %-4d %-4d %-6d %-7d %-4d %-4d %-4d%n",
                    "P" + p.getId(), p.getArrivalTime(), p.getBurstTime(),
                    p.getFirstStartTime(), p.getFinishTime(), tat, wt, rt);
        }

        int count = processes.size();
        System.out.printf("%nAverage TAT: %.2f%n", (double) totalTat / count);
        System.out.printf("Average WT:  %.2f%n", (double) totalWt / count);
        System.out.printf("Average RT:  %.2f%n", (double) totalRt / count);
    }
}
