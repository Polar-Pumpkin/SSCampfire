package org.serverct.parrot.sscampfire;

import org.serverct.parrot.parrotx.PPlugin;
import org.serverct.parrot.sscampfire.config.ConfigManager;
import org.serverct.parrot.sscampfire.task.RegenTask;

import java.math.BigDecimal;

public final class SSCampfire extends PPlugin {

    @Override
    protected void preload() {
        pConfig = ConfigManager.getInstance();
    }

    @Override
    protected void load() {
        final long cycle = BigDecimal.valueOf(ConfigManager.getInstance().getCycle() * 20L).longValue();
        new RegenTask(this).runTaskTimerAsynchronously(this, cycle, cycle);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        pConfig.save();
    }
}
