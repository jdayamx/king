package jday.king;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

class KingCubeBuilder$1 extends BukkitRunnable {
    int count;
    // $FF: synthetic field
    final int val$amount;
    // $FF: synthetic field
    final Player val$player;

    KingCubeBuilder$1(int var1, Player var2) {
        this.val$amount = var1;
        this.val$player = var2;
        this.count = 0;
    }

    public void run() {
        if (this.count < this.val$amount) {
            Location location = this.val$player.getLocation();
            World world = location.getWorld();
            if (world != null) {
                TNTPrimed tnt = (TNTPrimed)world.spawnEntity(location, EntityType.PRIMED_TNT);
                tnt.setYield(5.0F);
                tnt.setFuseTicks(5);
                tnt.setCustomName("tnt_ignore_damage");
                KingCubeBuilder.createFireworks(location, new Color[]{Color.BLUE, Color.YELLOW});
            }

            ++this.count;
        } else {
            this.cancel();
        }

    }
}