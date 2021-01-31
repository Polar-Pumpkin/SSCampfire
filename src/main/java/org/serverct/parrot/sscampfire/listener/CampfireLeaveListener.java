package org.serverct.parrot.sscampfire.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.serverct.parrot.parrotx.data.autoload.PAutoload;
import org.serverct.parrot.parrotx.utils.BasicUtil;
import org.serverct.parrot.sscampfire.config.ConfigManager;
import org.serverct.parrot.sscampfire.utils.MessageUtil;

import java.util.Objects;
import java.util.UUID;

@PAutoload
public class CampfireLeaveListener implements Listener {

    @EventHandler
    public void onLeaveCampfire(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (Objects.isNull(to) || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }
        final Player user = event.getPlayer();
        final UUID uuid = user.getUniqueId();

        final Location campfire = MessageUtil.getCampfire(uuid);
        if (Objects.isNull(campfire)
                || campfire.distance(to) < BasicUtil.roundToDouble(ConfigManager.getInstance().getRadius() * 2)) {
            return;
        }

        MessageUtil.leaveArea(uuid, campfire);
    }

}
