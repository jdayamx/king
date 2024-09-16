package jday.king;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import jday.king.KingCubeBuilder$1;

public class KingCubeBuilder {
    private static final List<Location> cubeLocations = new ArrayList();
    private static Location centerPlatformLocation = null;
    private static Location diamondBlockLocation = null;
    private static final List<Location> ladderLocations = new ArrayList();
    private static final ChatColor[] gradientColors;

    public static void buildCube(World world, Location centerLocation, int width, int height) {
        clearCube(world, centerLocation, width, height);
        centerPlatformLocation = centerLocation.clone();
        int halfWidth = width / 2;
        int sectionHeight = height / 3;

        int y;
        int x;
        for(y = 1; y <= height; ++y) {
            for(x = -halfWidth; x <= halfWidth; ++x) {
                for(int z = -halfWidth; z <= halfWidth; ++z) {
                    if (Math.abs(x) == halfWidth || Math.abs(z) == halfWidth) {
                        Location blockLocation = centerLocation.clone();
                        blockLocation.add((double)x, (double)y, (double)z);
                        if (y != sectionHeight && y != 2 * sectionHeight) {
                            blockLocation.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
                        } else if (y == sectionHeight) {
                            blockLocation.getBlock().setType(Material.GREEN_STAINED_GLASS);
                        } else if (y == 2 * sectionHeight) {
                            blockLocation.getBlock().setType(Material.RED_STAINED_GLASS);
                        }

                        cubeLocations.add(blockLocation);
                    }
                }
            }
        }

        for(y = -halfWidth; y <= halfWidth; ++y) {
            for(x = -halfWidth; x <= halfWidth; ++x) {
                Location blockLocation = centerLocation.clone();
                blockLocation.add((double)y, 0.0D, (double)x);
                blockLocation.getBlock().setType(Material.SMOOTH_QUARTZ);
                cubeLocations.add(blockLocation);
            }
        }

        Location upCenter = centerPlatformLocation.clone();
        upCenter.add(0.0D, (double)height, 0.0D);
        upCenter.getBlock().setType(Material.DIAMOND_BLOCK);
        cubeLocations.add(upCenter);
        buildSteps(centerPlatformLocation, halfWidth, height - 1, Material.STONE_BRICKS);
    }

    public static boolean isCubeLocations(Location location) {
        Iterator var1 = cubeLocations.iterator();

        Location storedLocation;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            storedLocation = (Location)var1.next();
        } while(storedLocation.getBlockX() != location.getBlockX() || storedLocation.getBlockY() != location.getBlockY() || storedLocation.getBlockZ() != location.getBlockZ() || !storedLocation.getWorld().equals(location.getWorld()));

        return true;
    }

    public static void newLadderMaterial(Material material) {
        Iterator var1 = ladderLocations.iterator();

        while(var1.hasNext()) {
            Location storedLocation = (Location)var1.next();
            Block block = storedLocation.getBlock();
            block.setType(material);
        }

    }

    public static void buildSteps(Location centerLocation, int halfWidth, int height, Material material) {
        int stepY = centerLocation.getBlockY() + height;
        diamondBlockLocation = centerLocation.clone();
        diamondBlockLocation.add(0.0D, (double)(height + 2), 0.0D);
        int Width = halfWidth - 1;
        int MaxX = centerLocation.getBlockX() + Width;
        int MaxZ = centerLocation.getBlockZ() + Width;
        int MinX = centerLocation.getBlockX() - Width;
        int MinZ = centerLocation.getBlockZ() - Width;

        int x;
        for(x = centerLocation.getBlockX() + 2; x <= MaxX; x += 2) {
            Location stepLocation = new Location(centerLocation.getWorld(), (double)x, (double)stepY, (double)centerLocation.getBlockZ());
            if (material != null) {
                stepLocation.getBlock().setType(material);
                cubeLocations.add(stepLocation);
                ladderLocations.add(stepLocation);
            } else {
                ladderLocations.add(stepLocation);
            }

            --stepY;
        }

        x = MaxX;
        int z = centerLocation.getBlockZ();

        for(int step = 2; stepY > centerLocation.getBlockY(); ++step) {
            if (x == MaxX && z < MaxZ) {
                ++z;
            } else if (z == MaxZ && x > MinX) {
                --x;
            } else if (x == MinX && z > MinZ) {
                --z;
            } else if (z == MinZ && x < MaxX) {
                ++x;
            } else if (x == MaxX && z > MaxZ) {
                --x;
            }

            if (step % 2 == 0) {
                Location stepLocation = new Location(centerLocation.getWorld(), (double)x, (double)stepY, (double)z);
                if (material != null) {
                    stepLocation.getBlock().setType(material);
                    cubeLocations.add(stepLocation);
                    ladderLocations.add(stepLocation);
                } else if (stepLocation.getBlock().getType() != Material.AIR) {
                    ladderLocations.add(stepLocation);
                }

                --stepY;
            }
        }

    }

    public static boolean isPlayerInGoldBlockZone(Player player) {
        Location playerLocation = player.getLocation();
        if (diamondBlockLocation == null) {
            return false;
        } else {
            return playerLocation.getBlockX() == diamondBlockLocation.getBlockX() && playerLocation.getBlockY() == diamondBlockLocation.getBlockY() && playerLocation.getBlockZ() == diamondBlockLocation.getBlockZ();
        }
    }

    public static void spawnTNT(King king, Player player, int amount, double delayTicks) {
        long Ticks = (long)(delayTicks * 20.0D);
        (new KingCubeBuilder$1(amount, player)).runTaskTimer(king, 0L, Ticks);
    }

    public static void createFireworks(Location location, Color... colors) {
        World world = (World)Objects.requireNonNull(location.getWorld());
        Firework firework = (Firework)world.spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        Builder effectBuilder = FireworkEffect.builder().with(Type.BURST);
        Color[] var6 = colors;
        int var7 = colors.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Color color = var6[var8];
            effectBuilder.withColor(color);
        }

        fireworkMeta.addEffect(effectBuilder.build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    public static void clearCube(World world, Location centerLocation, int width, int height) {
        cubeLocations.clear();
        ladderLocations.clear();
        centerPlatformLocation = null;
        diamondBlockLocation = null;
        int halfWidth = width / 2;

        for(int x = centerLocation.getBlockX() - halfWidth; x <= centerLocation.getBlockX() + halfWidth; ++x) {
            for(int y = centerLocation.getBlockY(); y <= centerLocation.getBlockY() + height; ++y) {
                for(int z = centerLocation.getBlockZ() - halfWidth; z <= centerLocation.getBlockZ() + halfWidth; ++z) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

    }

    public static void restoreArena(Location location, int width, int height) {
        if (location != null) {
            int halfWidth = width / 2;

            for(int x = location.getBlockX() - halfWidth; x <= location.getBlockX() + halfWidth; ++x) {
                for(int y = location.getBlockY(); y <= location.getBlockY() + height; ++y) {
                    for(int z = location.getBlockZ() - halfWidth; z <= location.getBlockZ() + halfWidth; ++z) {
                        Location blockLocation = new Location(location.getWorld(), (double)x, (double)y, (double)z);
                        if (blockLocation.getBlock().getType() != Material.AIR) {
                            cubeLocations.add(blockLocation);
                        }
                    }
                }
            }

            buildSteps(location, width / 2, height - 1, (Material)null);
            diamondBlockLocation = location.clone();
            diamondBlockLocation.add(0.0D, (double)(height + 1), 0.0D);
        }
    }

    public static void sendCountdownTitle(String title, Player player) {
        player.sendTitle(title, "", 10, 40, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

    public static void sendActionBar(Player player, String message, Boolean SoundEnable) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        if (SoundEnable) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 1.0F);
        }

    }

    public static String applyGradient(String text) {
        StringBuilder result = new StringBuilder();
        int colorIndex = 0;
        int colorLength = gradientColors.length;
        char[] var4 = text.toCharArray();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            char c = var4[var6];
            result.append(gradientColors[colorIndex % colorLength]);
            result.append(c);
            ++colorIndex;
        }

        return result.toString();
    }

    public static void EndTpPlayer(Player player) {
        if (diamondBlockLocation != null) {
            Location location = diamondBlockLocation.clone();
            player.teleport(location.add(0.0D, 1.0D, 0.0D));
        }

    }

    private static double getDistance(Location loc1, Location loc2) {
        return loc1.distance(loc2);
    }

    public static void movePlayerThreeCubesBack(Player player, int step) {
        Location playerLocation = player.getLocation();
        double nearestDistance = Double.MAX_VALUE;
        int nearestIndex = -1;

        int targetIndex;
        Location targetLocation;
        for(targetIndex = 0; targetIndex < ladderLocations.size(); ++targetIndex) {
            targetLocation = (Location)ladderLocations.get(targetIndex);
            double distance = getDistance(playerLocation, targetLocation);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = targetIndex;
            }
        }

        targetIndex = nearestIndex + step * -1;
        if (targetIndex < 0) {
            targetIndex = 0;
        } else if (targetIndex >= ladderLocations.size()) {
            targetIndex = ladderLocations.size() - 1;
        }

        targetLocation = ((Location)ladderLocations.get(targetIndex)).clone();
        targetLocation.setX((double)targetLocation.getBlockX() + 0.5D);
        targetLocation.setY((double)targetLocation.getBlockY() + 1.0D);
        targetLocation.setZ((double)targetLocation.getBlockZ() + 0.5D);
        player.teleport(targetLocation);
        checkAndRotatePlayer(player, targetLocation);
    }

    private static void checkAndRotatePlayer(Player player, Location targetLocation) {
        Location[] directions = new Location[]{targetLocation.clone().add(-1.0D, 0.0D, 0.0D), targetLocation.clone().add(1.0D, 0.0D, 0.0D), targetLocation.clone().add(0.0D, 0.0D, -1.0D), targetLocation.clone().add(0.0D, 0.0D, 1.0D)};
        Location[] var3 = directions;
        int var4 = directions.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Location direction = var3[var5];
            if (direction.getBlock().getType() != Material.AIR) {
                faceBlockAndRotateLeft(player, direction);
                break;
            }
        }

    }

    private static void faceBlockAndRotateLeft(Player player, Location blockLocation) {
        Location playerLocation = player.getLocation();
        double xDiff = blockLocation.getX() - playerLocation.getX();
        double zDiff = blockLocation.getZ() - playerLocation.getZ();
        float yaw = 0.0F;
        if (xDiff > 0.0D) {
            yaw = -90.0F;
        } else if (xDiff < 0.0D) {
            yaw = 90.0F;
        } else if (zDiff > 0.0D) {
            yaw = 0.0F;
        } else if (zDiff < 0.0D) {
            yaw = 180.0F;
        }

        playerLocation.setYaw(yaw);
        playerLocation.setYaw(playerLocation.getYaw() - 90.0F);
        player.teleport(playerLocation);
    }

    static {
        gradientColors = new ChatColor[]{ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN};
    }
}
