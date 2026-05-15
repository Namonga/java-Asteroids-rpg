package org.com.gamep.game;

public class Mission {
    public enum Type { KILL_SCOUTS, KILL_TANKS, COLLECT_LOOT, DESTROY_ASTEROIDS, BOUNTY }

    private Type type;
    private int targetAmount;
    private int currentProgress;
    private int rewardScrap;
    private int rewardCells;
    private int rewardAlloy;
    private String description;
    private String bountyName;
    private double timeLimit;

    public Mission(Type type, int targetAmount, int rewardScrap, int rewardCells, int rewardAlloy) {
        this(type, targetAmount, rewardScrap, rewardCells, rewardAlloy, null, 0);
    }

    public Mission(Type type, int targetAmount, int rewardScrap, int rewardCells, int rewardAlloy,
                   String bountyName, double timeLimit) {
        this.type = type;
        this.targetAmount = targetAmount;
        this.rewardScrap = rewardScrap;
        this.rewardCells = rewardCells;
        this.rewardAlloy = rewardAlloy;
        this.bountyName = bountyName;
        this.timeLimit = timeLimit;
        this.currentProgress = 0;
        this.description = generateDescription();
    }

    private String generateDescription() {
        switch (type) {
            case KILL_SCOUTS: return "Уничтожить " + targetAmount + " скаутов";
            case KILL_TANKS: return "Уничтожить " + targetAmount + " танков";
            case COLLECT_LOOT: return "Собрать " + targetAmount + " ед. лута";
            case DESTROY_ASTEROIDS: return "Уничтожить " + targetAmount + " астероидов";
            case BOUNTY: return "Охота: " + bountyName;
            default: return "???";
        }
    }

    public Type getType() { return type; }
    public String getDescription() { return description; }
    public int getCurrentProgress() { return currentProgress; }
    public int getTargetAmount() { return targetAmount; }
    public int getRewardScrap() { return rewardScrap; }
    public int getRewardCells() { return rewardCells; }
    public int getRewardAlloy() { return rewardAlloy; }
    public boolean isCompleted() { return currentProgress >= targetAmount; }
    public String getBountyName() { return bountyName; }
    public double getTimeLimit() { return timeLimit; }

    public void addProgress(int amount) {
        currentProgress = Math.min(currentProgress + amount, targetAmount);
    }

    public void reduceTime(double delta) {
        if (timeLimit > 0) {
            timeLimit -= delta;
        }
    }

    public boolean isTimeExpired() {
        return type == Type.BOUNTY && timeLimit <= 0;
    }
}