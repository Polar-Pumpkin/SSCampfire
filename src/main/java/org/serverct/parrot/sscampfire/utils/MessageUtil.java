package org.serverct.parrot.sscampfire.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class MessageUtil {

    private static final Multimap<Location, UUID> MESSAGE_MAP = HashMultimap.create();

    public static boolean shouldMessage(@NotNull final UUID uuid, @NotNull final Location campfire) {
        final boolean should = !MESSAGE_MAP.get(campfire).contains(uuid);
        if (should) {
            MESSAGE_MAP.put(campfire, uuid);
        }
        return should;
    }

    public static void leaveArea(@NotNull final UUID uuid, @NotNull final Location campfire) {
        MESSAGE_MAP.remove(campfire, uuid);
    }

    @Nullable
    public static Location getCampfire(final UUID uuid) {
        for (Map.Entry<Location, Collection<UUID>> entry : MESSAGE_MAP.asMap().entrySet()) {
            if (entry.getValue().contains(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
