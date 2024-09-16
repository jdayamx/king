package jday.king;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class King extends JavaPlugin implements TabCompleter {
    public final int MIN_WIDTH_HEIGHT = 14;
    public final int MIN_HEIGHT = 30;

    @Override
    public void onEnable() {
        // Plugin startup logic
        KingCommandExecutor commandExecutor = new KingCommandExecutor(this);
        ((PluginCommand)Objects.requireNonNull(this.getCommand("king"))).setExecutor(commandExecutor);
        ((PluginCommand)Objects.requireNonNull(this.getCommand("king"))).setTabCompleter(this);
        this.getServer().getPluginManager().registerEvents(commandExecutor, this);
        commandExecutor.loadArenaConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public List<String> onTabComplete(@NonNull CommandSender sender, Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList();
        if (command.getName().equalsIgnoreCase("king")) {
            if (args.length == 1) {
                completions.add("create 14 30");
                completions.add("tnt 1");
                completions.add("delete");
                completions.add("edit");
                completions.add("tp");
                completions.add("setStepMaterial");
                completions.add("options");
                completions.add("finishTp");
                completions.add("movePlayer 1");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("setStepMaterial")) {
                String materialPrefix = args[1].toUpperCase();
                Material[] var7 = Material.values();
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    Material material = var7[var9];
                    if (material.isBlock() && material.name().startsWith(materialPrefix)) {
                        completions.add(material.name());
                    }
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("options")) {
                completions.add("tntActiveDamage");
                completions.add("TimeTntSpawnNext 0.3");
                completions.add("soundEnable");
                completions.add("winText Win!!!");
            }

            if (args.length == 3 && args[1].equalsIgnoreCase("TimeTntSpawnNext")) {
                completions.add("0.1");
                completions.add("0.2");
                completions.add("0.3");
                completions.add("0.4");
                completions.add("0.5");
                completions.add("0.6");
            }
        }

        return completions;
    }
}
