import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ACSTrace {
    private static class Entry {
        final int time;
        final String running;
        final String tier1;
        final String tier2;
        final String demoted;

        Entry(int time, String running, String tier1, String tier2, String demoted) {
            this.time = time;
            this.running = running;
            this.tier1 = tier1;
            this.tier2 = tier2;
            this.demoted = demoted;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private String pendingDemotion = "-";

    public void clear() {
        entries.clear();
        pendingDemotion = "-";
    }

    public void demote(String label) {
        pendingDemotion = label;
    }

    public void add(int time, String running, Collection<Process> tier1, List<Process> tier2) {
        entries.add(new Entry(
                time,
                running,
                formatTier1(tier1),
                formatTier2(tier2),
                pendingDemotion
        ));
        pendingDemotion = "-";
    }

    private String formatTier1(Collection<Process> processes) {
        if (processes.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (Process p : processes) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("P").append(p.getId())
                    .append("(rem=").append(p.getRemainingTime()).append(")");
        }
        return sb.toString();
    }

    private String formatTier2(List<Process> processes) {
        if (processes.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (Process p : processes) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("P").append(p.getId())
                    .append("(rem=").append(p.getRemainingTime())
                    .append(", prio=").append(p.getPriority()).append(")");
        }
        return sb.toString();
    }

    public void print() {
        String[] headers = {"Time", "Running", "Tier 1 Ready", "Tier 2 Waiting", "Demoted T1->T2"};
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        for (Entry e : entries) {
            widths[0] = Math.max(widths[0], String.valueOf(e.time).length());
            widths[1] = Math.max(widths[1], e.running.length());
            widths[2] = Math.max(widths[2], e.tier1.length());
            widths[3] = Math.max(widths[3], e.tier2.length());
            widths[4] = Math.max(widths[4], e.demoted.length());
        }

        String format = "%-" + widths[0] + "s  %-" + widths[1] + "s  %-" + widths[2]
                + "s  %-" + widths[3] + "s  %-" + widths[4] + "s%n";

        System.out.printf(format, "Time", "Running", "Tier 1 Ready", "Tier 2 Waiting", "Demoted T1->T2");
        int totalWidth = widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + 8;
        System.out.println("-".repeat(totalWidth));

        for (Entry e : entries) {
            System.out.printf(format,
                    String.valueOf(e.time), e.running, e.tier1, e.tier2, e.demoted);
        }
    }
}
