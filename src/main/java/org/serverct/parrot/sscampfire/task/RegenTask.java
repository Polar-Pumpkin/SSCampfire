package org.serverct.parrot.sscampfire.task;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.serverct.parrot.parrotx.PPlugin;
import org.serverct.parrot.sscampfire.config.ConfigManager;
import org.serverct.parrot.sscampfire.utils.MessageUtil;

import java.math.BigDecimal;
import java.util.*;

public class RegenTask extends BukkitRunnable {

    private final PPlugin plugin;
    private final Random random = new Random();
    private final ConfigManager config = ConfigManager.getInstance();
    private final Map<UUID, Boolean> cooldownMap = new HashMap<>();


    public RegenTask(PPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final double radius = config.getRadius();
        final List<Location> campfires = new ArrayList<>(config.getCampfires());

        for (Location campfire : campfires) {
            final Block block = campfire.getBlock();
            if (!Material.CAMPFIRE.equals(block.getType())) {
                config.deleteCampfire(campfire);
                continue;
            }
            Campfire data = (Campfire) block.getBlockData();
            if (data.isWaterlogged()) {
                continue;
            }

            for (Player user : Bukkit.getOnlinePlayers()) {
                if (user.isDead()) {
                    continue;
                }

                if (campfire.distance(user.getLocation()) <= radius) {
                    final UUID uuid = user.getUniqueId();
                    final double currentHealth = user.getHealth();
                    double maxHealth;

                    final AttributeInstance maxHealthAttr = user.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (Objects.isNull(maxHealthAttr)) {
                        maxHealth = 20.0D;
                    } else {
                        maxHealth = maxHealthAttr.getValue();
                    }

                    if (currentHealth >= maxHealth || this.cooldownMap.getOrDefault(uuid, false)) {
                        continue;
                    }
                    final long cooldown = BigDecimal.valueOf(config.getCooldown() * 20L).longValue();

                    final double afterRegen = currentHealth + config.getRandomRegen(this.random);
                    if (afterRegen >= maxHealth) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                MessageUtil.leaveArea(uuid, campfire);
                            }
                        }.runTaskLaterAsynchronously(plugin, cooldown);
                    }

                    user.setHealth(Math.min(afterRegen, maxHealth));
                    this.cooldownMap.put(uuid, true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cooldownMap.put(uuid, false);
                        }
                    }.runTaskLaterAsynchronously(plugin, cooldown);

                    if (MessageUtil.shouldMessage(uuid, campfire)) {
                        final String message = config.getRandomSentence(this.random);
                        if (Objects.nonNull(message)) {
                            plugin.getLang().sender.infoMessage(user, message);
                        }
                    }

                    final World campfireWorld = campfire.getWorld();
                    if (Objects.nonNull(campfireWorld)) {
                        campfireWorld.spawnParticle(Particle.VILLAGER_HAPPY, campfire, 10, 0.5, 0.5, 0.5);
                    }
                    user.getWorld().spawnParticle(Particle.HEART, user.getLocation(), 10, 0.5, 0.5, 0.5);
                }
            }
        }
    }
}
