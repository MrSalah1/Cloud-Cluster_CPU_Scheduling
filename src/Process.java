public class Process {
    private int id;
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int priority;
    private final int initialPriority;
    private int firstStartTime;
    private int finishTime;
    private int tier2EntryTime = -1;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.initialPriority = priority;
        this.firstStartTime = -1;
        this.finishTime = -1;
    }

    public void executeOneUnit() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public void recordFirstStart(int time) {
        if (firstStartTime == -1) {
            firstStartTime = time;
        }
    }

    public void recordFinish(int time) {
        finishTime = time;
    }

    public boolean isFinished() {
        return remainingTime == 0;
    }

    // will be used in aging tier
    public void recordTier2Entry(int time) {
        tier2EntryTime = time;
    }
    // will be used in aging tier
    public int getTier2EntryTime() {
        return tier2EntryTime;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getInitialPriority() {
        return initialPriority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getFirstStartTime() {
        return firstStartTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getTurnaroundTime() {
        return finishTime - arrivalTime;
    }

    public int getWaitingTime() {
        return getTurnaroundTime() - burstTime;
    }

    public int getResponseTime() {
        return firstStartTime - arrivalTime;
    }
}
