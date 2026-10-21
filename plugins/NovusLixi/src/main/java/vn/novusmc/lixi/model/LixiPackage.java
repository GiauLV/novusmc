package vn.novusmc.lixi.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LixiPackage {
    private final String id;
    private final UUID ownerUuid;
    private final String ownerName;
    private final String currency;
    private final double totalFund;
    private final int totalSlots;
    private final double slotValue;
    private final long expireTimeMillis;
    private final Set<UUID> claimedPlayers = new HashSet<>();
    private int remainingSlots;

    public LixiPackage(String id, UUID ownerUuid, String ownerName, String currency, double totalFund, int totalSlots, long expireTimeMillis) {
        this.id = id;
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.currency = currency;
        this.totalFund = totalFund;
        this.totalSlots = totalSlots;
        this.remainingSlots = totalSlots;
        this.slotValue = Math.floor(totalFund / totalSlots);
        this.expireTimeMillis = expireTimeMillis;
    }

    public String getId() { return id; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getOwnerName() { return ownerName; }
    public String getCurrency() { return currency; }
    public double getTotalFund() { return totalFund; }
    public int getTotalSlots() { return totalSlots; }
    public double getSlotValue() { return slotValue; }
    public long getExpireTimeMillis() { return expireTimeMillis; }
    public Set<UUID> getClaimedPlayers() { return claimedPlayers; }
    public int getRemainingSlots() { return remainingSlots; }

    public synchronized boolean claim(UUID playerUuid) {
        if (remainingSlots <= 0 || claimedPlayers.contains(playerUuid) || ownerUuid.equals(playerUuid)) {
            return false;
        }
        claimedPlayers.add(playerUuid);
        remainingSlots--;
        return true;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTimeMillis;
    }
}
