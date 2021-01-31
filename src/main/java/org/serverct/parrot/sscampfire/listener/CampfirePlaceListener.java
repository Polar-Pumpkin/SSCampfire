package org.serverct.parrot.sscampfire.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.serverct.parrot.parrotx.data.autoload.PAutoload;
import org.serverct.parrot.parrotx.utils.BasicUtil;
import org.serverct.parrot.sscampfire.config.ConfigManager;

import java.util.Objects;

@PAutoload
public class CampfirePlaceListener implements Listener {

    @EventHandler
    public void onCampfirePlace(BlockPlaceEvent event) {
        final ConfigManager config = ConfigManager.getInstance();
        final double existRange = BasicUtil.roundToDouble(config.getRadius() * 3);

        final Block block = event.getBlock();
        final Location location = block.getLocation();

        if (!Material.CAMPFIRE.equals(block.getType())) {
            return;
        }

        boolean exist = false;
        for (Location campfire : config.getCampfires()) {
            if (location.distance(campfire) <= existRange) {
                exist = true;
            }
        }

        if (!exist) {
            ConfigManager.getInstance().newCampfire(location);
            final World world = location.getWorld();
            if (Objects.nonNull(world)) {
                world.spawnParticle(Particle.VILLAGER_HAPPY, location, 10, 0.5, 0.5, 0.5);
            }
        }
    }

    @EventHandler
    public void onCampfireBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (!Material.CAMPFIRE.equals(block.getType())) {
            return;
        }
        ConfigManager.getInstance().deleteCampfire(block.getLocation());
    }
}
