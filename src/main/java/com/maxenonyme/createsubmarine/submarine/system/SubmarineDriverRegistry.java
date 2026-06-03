package com.maxenonyme.createsubmarine.submarine.system;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SubmarineDriverRegistry {
    public static final int HULL_CONTROLLER = 2;
    public static final int OXYGEN_DIFFUSER = 1;

    private static final long STALE_TICKS = 60;

    private record Claim(BlockPos pos, int priority, long lastTick) {}

    private static final Map<UUID, Map<BlockPos, Claim>> CLAIMS = new ConcurrentHashMap<>();

    public static boolean claim(UUID id, BlockPos pos, int priority, long tick) {
        BlockPos key = pos.immutable();
        CLAIMS.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(key, new Claim(key, priority, tick));
        return isOwner(id, key, tick);
    }

    public static boolean isOwner(UUID id, BlockPos pos, long tick) {
        Claim best = bestClaim(id, tick);
        return best != null && best.pos().equals(pos);
    }

    public static boolean release(UUID id, BlockPos pos, long tick) {
        Map<BlockPos, Claim> subClaims = CLAIMS.get(id);
        if (subClaims == null) return true;
        subClaims.remove(pos.immutable());
        boolean anyFresh = false;
        for (Claim c : subClaims.values()) {
            if (tick - c.lastTick() <= STALE_TICKS) {
                anyFresh = true;
                break;
            }
        }
        if (subClaims.isEmpty()) CLAIMS.remove(id);
        return !anyFresh;
    }

    private static Claim bestClaim(UUID id, long tick) {
        Map<BlockPos, Claim> subClaims = CLAIMS.get(id);
        if (subClaims == null) return null;
        Claim best = null;
        for (Claim c : subClaims.values()) {
            if (tick - c.lastTick() > STALE_TICKS) continue;
            if (best == null || c.priority() > best.priority()
                    || (c.priority() == best.priority() && lex(c.pos(), best.pos()) < 0)) {
                best = c;
            }
        }
        return best;
    }

    private static int lex(BlockPos a, BlockPos b) {
        int dx = Integer.compare(a.getX(), b.getX());
        if (dx != 0) return dx;
        int dy = Integer.compare(a.getY(), b.getY());
        if (dy != 0) return dy;
        return Integer.compare(a.getZ(), b.getZ());
    }

    public static void clearAll() {
        CLAIMS.clear();
    }
}
