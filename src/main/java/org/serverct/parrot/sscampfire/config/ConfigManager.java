package org.serverct.parrot.sscampfire.config;

import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.serverct.parrot.parrotx.api.ParrotXAPI;
import org.serverct.parrot.parrotx.config.PConfig;
import org.serverct.parrot.parrotx.data.autoload.Group;
import org.serverct.parrot.parrotx.data.autoload.Load;
import org.serverct.parrot.parrotx.utils.BasicUtil;
import org.serverct.parrot.sscampfire.SSCampfire;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Group(path = "Settings")
public class ConfigManager extends PConfig {

    private static ConfigManager instance;
    @Getter
    private final List<Location> campfires = new ArrayList<>();
    private final List<String> sentences = new ArrayList<>();
    @Load(path = "Regen")
    private String range;
    @Load(path = "MaxRegen")
    private double maxRegen;
    @Load(path = "Distance")
    private double radius;
    @Load(path = "Cycle")
    private double cycle;
    @Getter
    @Load(path = "Cooldown")
    private double cooldown;

    private double min;
    private double max;
    private double offset;

    public ConfigManager() {
        super(ParrotXAPI.getPlugin(SSCampfire.class), "config", "主配置文件");
    }

    public static ConfigManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ConfigManager();
        }
        return instance;
    }

    @Override
    public void load() {
        super.load();

        this.sentences.addAll(config.getStringList("Sentences"));
        if (Objects.isNull(this.range)) {
            this.range = "1-3";
        }
        final String[] nums = this.range.split("[~]");
        if (nums.length <= 1) {
            final double onlyValue = Double.parseDouble(nums[0]);
            this.max = onlyValue;
            this.min = onlyValue;
            this.offset = 0.0D;
        } else {
            final double num1 = Double.parseDouble(nums[0]);
            final double num2 = Double.parseDouble(nums[1]);
            this.min = Math.min(num1, num2);
            this.max = Math.max(num1, num2);
            this.offset = max - min;
        }

        final ConfigurationSection section = config.getConfigurationSection("Campfires");
        if (Objects.isNull(section)) {
            return;
        }
        section.getKeys(false).forEach(key -> {
            final Location loc = section.getLocation(key);
            if (Objects.isNull(loc)) {
                section.set(key, null);
            } else {
                this.campfires.add(loc);
            }
        });
    }

    @Override
    public void save() {
        // 之所以每次 save 都新建 Section 重新随机 Key 保存 Location
        // 是因为读取 Location 的时候并没有读 Key
        // 在移除一个 Location 之后要想单独在数据文件里面移除它没有 Key
        ConfigurationSection section = config.createSection("Campfires");
        for (Location loc : this.campfires) {
            final String key = RandomStringUtils.randomAlphanumeric(30);
            if (section.isConfigurationSection(key)) {
                // FIXME 这里可能有一个 key 重复的问题，我懒得用 while 循环直到不重复了，反正长度 30 的随机字符串，重复概率不大。
            }
            section.set(key, loc);
        }
        super.save();
    }

    public void newCampfire(final Location location) {
        if (Objects.isNull(location)) {
            return;
        }
        this.campfires.add(location);
    }

    public void deleteCampfire(final Location location) {
        this.campfires.remove(location);
    }

    @Nullable
    public String getRandomSentence(final Random random) {
        if (this.sentences.isEmpty()) {
            return null;
        }
        return this.sentences.get(random.nextInt(this.sentences.size()));
    }

    public double getRandomRegen(final Random random) {
        return BasicUtil.roundToDouble(Math.min(this.max, this.min + (this.offset + random.nextDouble())));
    }

    public double getRadius() {
        if (radius <= 0) {
            return 4.0D; // 这是默认值
        }
        return radius;
    }

    public double getCycle() {
        if (cycle <= 0) {
            return 2.5D;
        }
        return cycle;
    }

    public double getMaxRegen() {
        if (maxRegen <= 0) {
            return 0.7D;
        }
        return Math.min(maxRegen, 1.0D);
    }
}
