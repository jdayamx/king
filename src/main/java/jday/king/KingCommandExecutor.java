package jday.king;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
//import org.example.boostik.king.KingCommandExecutor.1;

public class KingCommandExecutor implements CommandExecutor, Listener {
    private Location arenaLocation;
    private int arenaHeight;
    private int arenaWidth;
    private final King king;
    private final FileConfiguration config;
    private int countdownStart = 10;
    private BukkitRunnable countdownTask;
    private int countdownTime;
    private boolean isPlatformEditable = false;
    private int countdownInterval = 0;
    private boolean isTntDamageEnabled = true;
    private String WinText = "Win!!";
    private double tickTntSpawn = 0.3D;
    private double lastCenterY = 0.0D;
    private boolean isSoundEnabled = true;

    KingCommandExecutor(King plugin) {
        this.king = plugin;
        Objects.requireNonNull(this.king);
        this.arenaHeight = 32;
        Objects.requireNonNull(this.king);
        this.arenaWidth = 16;
        this.config = this.king.getConfig();
        this.config.options().copyDefaults(true);
        this.king.saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("king")) {
            Player player;
            if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }

                player = (Player)sender;
                if (args.length < 3) {
                    player.sendMessage("Usage: /king create <width> <height> . Help Go to the website: https://boostik.in");
                    return true;
                }

                try {
                    if (this.arenaLocation != null) {
                        KingCubeBuilder.clearCube(player.getLocation().getWorld(), this.arenaLocation, this.arenaWidth, this.arenaHeight);
                        this.arenaLocation = null;
                    }

                    Location centerLocation = player.getLocation().getBlock().getLocation();
                    int width = Integer.parseInt(args[1]);
                    int height = Integer.parseInt(args[2]);
                    Objects.requireNonNull(this.king);
                    if (width >= 14) {
                        Objects.requireNonNull(this.king);
                        if (height >= 30) {
                            this.saveArenaConfig(player.getLocation().clone(), height, width);
                            this.arenaLocation = centerLocation.clone();
                            World world = player.getWorld();
                            KingCubeBuilder.buildCube(world, centerLocation.clone(), width, height);
                            Location tp = centerLocation.clone();
                            player.teleport(tp.add(0.0D, 1.0D, 0.0D));
                            player.sendMessage("Cube created successfully.");
                            if (this.isPlatformEditable) {
                                this.isPlatformEditable = false;
                            }

                            return true;
                        }
                    }

                    String[] var10001 = new String[2];
                    StringBuilder var10004 = (new StringBuilder()).append("Minimum width: ");
                    Objects.requireNonNull(this.king);
                    var10004 = var10004.append(14).append(", Minimum height: ");
                    Objects.requireNonNull(this.king);
                    var10001[0] = var10004.append(30).toString();
                    var10001[1] = "Go to the website: https://boostik.in";
                    player.sendMessage(var10001);
                    return true;
                } catch (NumberFormatException var11) {
                    player.sendMessage(new String[]{"Invalid number format. Usage: /king create <width> <height>", "Go to the website: https://boostik.in"});
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("options") && args.length > 1) {
                if (!(sender instanceof Player)) {
                    return true;
                }

                if (args[1].equalsIgnoreCase("soundEnable")) {
                    this.isSoundEnabled = !this.isSoundEnabled;
                    sender.sendMessage("Sound Enabled: " + this.isSoundEnabled);
                    return true;
                }

                if (args[1].equalsIgnoreCase("TimeTntSpawnNext")) {
                    if (args.length == 3) {
                        this.tickTntSpawn = Double.parseDouble(args[2]);
                        this.config.set("timeNextTnt", this.tickTntSpawn);
                        this.king.saveConfig();
                        sender.sendMessage(ChatColor.YELLOW + "New time spawn tnt :" + ChatColor.GOLD + args[2]);
                    }

                    return true;
                }

                if (args[1].equalsIgnoreCase("tntActiveDamage")) {
                    player = (Player)sender;
                    this.isTntDamageEnabled = !this.isTntDamageEnabled;
                    if (this.isTntDamageEnabled) {
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage("CREATIVE");
                    } else {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage("SURVIVAL");
                    }

                    this.config.set("tntdamage", this.isTntDamageEnabled);
                    this.king.saveConfig();
                    player.sendMessage("TNT damage " + (this.isTntDamageEnabled ? "enabled" : "disabled") + ".");
                    player.setAllowFlight(true);
                    return true;
                }

                if (args[1].equalsIgnoreCase("winText")) {
                    this.config.set("wintext", args[2]);
                    this.WinText = args[2];
                    sender.sendMessage(ChatColor.YELLOW + "New title winText: " + ChatColor.GOLD + this.WinText);
                    return true;
                }
            }

            Iterator var13;
            if (args[0].equalsIgnoreCase("tnt")) {
                var13 = Bukkit.getOnlinePlayers().iterator();

                while(var13.hasNext()) {
                    player = (Player)var13.next();
                    if (args.length == 2) {
                        KingCubeBuilder.spawnTNT(this.king, player, Integer.parseInt(args[1]), this.tickTntSpawn);
                    }

                    if (args.length == 3) {
                        KingCubeBuilder.spawnTNT(this.king, player, Integer.parseInt(args[1]), Double.parseDouble(args[2]));
                    }
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("finishTp")) {
                var13 = Bukkit.getOnlinePlayers().iterator();

                while(var13.hasNext()) {
                    player = (Player)var13.next();
                    KingCubeBuilder.EndTpPlayer(player);
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("movePlayer") && args.length == 2) {
                var13 = Bukkit.getOnlinePlayers().iterator();

                while(var13.hasNext()) {
                    player = (Player)var13.next();
                    KingCubeBuilder.movePlayerThreeCubesBack(player, Integer.parseInt(args[1]));
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                var13 = Bukkit.getOnlinePlayers().iterator();

                while(var13.hasNext()) {
                    player = (Player)var13.next();
                    Location tp = this.arenaLocation.clone();
                    player.teleport(tp.add(0.0D, 1.0D, 0.0D));
                }

                return true;
            }

            if (this.arenaLocation == null) {
                sender.sendMessage("No arena to clear.");
                return true;
            }

            if (args[0].equalsIgnoreCase("edit")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }

                player = (Player)sender;
                this.isPlatformEditable = !this.isPlatformEditable;
                player.sendMessage("Platform editing " + (this.isPlatformEditable ? "enabled" : "disabled") + ".");
                return true;
            }

            if (args[0].equalsIgnoreCase("setStepMaterial")) {
                if (!(sender instanceof Player)) {
                    return true;
                }

                if (args.length > 1) {
                    Material material = Material.matchMaterial(args[1]);
                    if (material != null) {
                        KingCubeBuilder.newLadderMaterial(material);
                    } else {
                        sender.sendMessage("Invalid material: " + args[1]);
                    }
                } else {
                    sender.sendMessage("Please specify a material.");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!this.isPlatformEditable) {
                    this.isPlatformEditable = true;
                }

                if (sender instanceof Player) {
                    player = (Player)sender;
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        player.setAllowFlight(true);
                    } else if (((Player)sender).getGameMode() == GameMode.SURVIVAL) {
                        player.setAllowFlight(false);
                    }

                    World world = this.arenaLocation.getWorld();
                    if (world != null) {
                        KingCubeBuilder.clearCube(world, this.arenaLocation, this.arenaWidth, this.arenaHeight);
                        this.clearConfig();
                        this.arenaLocation = null;
                        sender.sendMessage("Cube cleared successfully. Go to the website: https://boostik.in");
                    } else {
                        sender.sendMessage("Error clearing cube: world not found.");
                    }
                }

                return true;
            }
        }

        return false;
    }

    public void loadArenaConfig() {
        if (this.config.contains("timer")) {
            this.countdownStart = this.config.getInt("timer");
        }

        if (this.config.contains("timeNextTnt")) {
            this.tickTntSpawn = this.config.getDouble("timeNextTnt");
        }

        if (this.config.contains("tntdamage")) {
            this.isTntDamageEnabled = this.config.getBoolean("tntdamage");
        }

        if (this.config.contains("wintext")) {
            this.WinText = this.config.getString("wintext");
        }

        if (this.config.contains("arena")) {
            String[] locData = ((String)Objects.requireNonNull(this.config.getString("arena"))).split(",");
            if (locData.length == 4) {
                World world = Bukkit.getWorld(locData[0]);
                int x = Integer.parseInt(locData[1]);
                int y = Integer.parseInt(locData[2]);
                int z = Integer.parseInt(locData[3]);
                this.arenaLocation = new Location(world, (double)x, (double)y, (double)z);
                this.arenaHeight = this.config.getInt("height");
                this.arenaWidth = this.config.getInt("width");
                KingCubeBuilder.restoreArena(this.arenaLocation, this.arenaWidth, this.arenaHeight);
            }
        }

    }

    private void clearConfig() {
        this.config.set("arena", (Object)null);
        this.config.set("height", (Object)null);
        this.config.set("width", (Object)null);
        this.king.saveConfig();
    }

    private void saveArenaConfig(Location playerLocation, int arenaHeight, int arenaWight) {
        if (playerLocation != null) {
            int x = playerLocation.getBlockX();
            int y = playerLocation.getBlockY();
            int z = playerLocation.getBlockZ();
            String locData = ((World)Objects.requireNonNull(playerLocation.getWorld())).getName() + "," + x + "," + y + "," + z;
            this.config.set("arena", locData);
            this.config.set("height", arenaHeight);
            this.config.set("width", arenaWight);
            this.config.set("timer", this.countdownStart);
            this.config.set("tntdamage", this.isTntDamageEnabled);
            this.config.set("wintext", this.WinText);
            this.config.set("timeNextTnt", this.tickTntSpawn);
            this.king.saveConfig();
            this.arenaLocation = playerLocation.clone();
            this.arenaHeight = arenaHeight;
            this.arenaWidth = arenaWight;
        }

    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList();
        Iterator var3 = event.blockList().iterator();

        while(var3.hasNext()) {
            Block block = (Block)var3.next();
            if (this.isInsideBox(block.getLocation())) {
                blocksToRemove.add(block);
            }
        }

        event.blockList().removeAll(blocksToRemove);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (this.isInsideBox(block.getLocation()) && !this.isPlatformEditable && KingCubeBuilder.isCubeLocations(block.getLocation())) {
            event.setCancelled(true);
        }

    }

    private boolean isInsideBox(Location location) {
        if (this.arenaLocation == null) {
            return false;
        } else {
            World world = this.arenaLocation.getWorld();
            if (!Objects.equals(location.getWorld(), world)) {
                return false;
            } else {
                int minX = this.arenaLocation.getBlockX() - this.arenaWidth / 2;
                int maxX = this.arenaLocation.getBlockX() + this.arenaWidth / 2;
                int minY = this.arenaLocation.getBlockY();
                int maxY = this.arenaLocation.getBlockY() + this.arenaHeight;
                int minZ = this.arenaLocation.getBlockZ() - this.arenaWidth / 2;
                int maxZ = this.arenaLocation.getBlockZ() + this.arenaWidth / 2;
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        double currentY = (double)player.getLocation().getBlockY();
        if (currentY != this.lastCenterY && this.arenaLocation != null) {
            this.lastCenterY = currentY;
            int remainingHeight = (int)Math.abs(currentY - (double)this.arenaLocation.getBlockY() - 1.0D);
            String text = ChatColor.YELLOW + String.valueOf(remainingHeight) + "/" + this.arenaHeight;
            KingCubeBuilder.sendActionBar(player, text, this.isSoundEnabled);
        }

        if (KingCubeBuilder.isPlayerInGoldBlockZone(player)) {
            if (this.countdownTask == null) {
                this.startCountdown(player);
            }
        } else if (this.countdownTask != null) {
            this.stopCountdown(player);
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.isTntDamageEnabled) {
            Player player = event.getPlayer();
            player.setAllowFlight(true);
            player.setFlying(false);
        }

    }

    private void startCountdown(Player player) {
        this.countdownTime = this.countdownStart;
        this.countdownTask = new KingCommandExecutor$1(this, player);
        this.countdownTask.runTaskTimer(this.king, 0L, 20L);
    }

    private void stopCountdown(Player player) {
        if (this.countdownTask != null) {
            this.countdownTask.cancel();
            this.countdownTask = null;
            this.countdownInterval = 0;
        }

        this.countdownTime = this.countdownStart;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (event.getCause() == DamageCause.ENTITY_EXPLOSION && !this.isPlatformEditable) {
            if (entity instanceof TNTPrimed && entity.getCustomName() != null && entity.getCustomName().equals("tnt_ignore_damage")) {
                event.setCancelled(true);
            } else {
                event.setDamage(0.0D);
            }
        }

    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL && !this.isPlatformEditable) {
            event.setCancelled(true);
            Player player = (Player)event.getEntity();
            player.damage(-event.getDamage());
        }

    }

    // $FF: synthetic method
    static int access$000(KingCommandExecutor x0) {
        return x0.countdownTime;
    }

    // $FF: synthetic method
    static int access$006(KingCommandExecutor x0) {
        return --x0.countdownTime;
    }

    // $FF: synthetic method
    static int access$100(KingCommandExecutor x0) {
        return x0.countdownInterval;
    }

    // $FF: synthetic method
    static String access$200(KingCommandExecutor x0) {
        return x0.WinText;
    }

    // $FF: synthetic method
    static Location access$300(KingCommandExecutor x0) {
        return x0.arenaLocation;
    }

    // $FF: synthetic method
    static BukkitRunnable access$402(KingCommandExecutor x0, BukkitRunnable x1) {
        return x0.countdownTask = x1;
    }

    // $FF: synthetic method
    static int access$104(KingCommandExecutor x0) {
        return ++x0.countdownInterval;
    }
}