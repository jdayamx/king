package jday.king;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class KingCommandExecutor$1 extends BukkitRunnable {
    // $FF: synthetic field
    final Player val$player;
    // $FF: synthetic field
    final KingCommandExecutor this$0;

    KingCommandExecutor$1(KingCommandExecutor this$0, Player var2) {
        this.this$0 = this$0;
        this.val$player = var2;
    }

    public void run() {
        if (KingCommandExecutor.access$000(this.this$0) > 3) {
            KingCommandExecutor.access$006(this.this$0);
            KingCubeBuilder.sendCountdownTitle(KingCommandExecutor.access$000(this.this$0) == 3 ? ChatColor.RED + "" + KingCommandExecutor.access$000(this.this$0) : "" + KingCommandExecutor.access$000(this.this$0), this.val$player);
        } else if (KingCommandExecutor.access$100(this.this$0) % 3 == 0) {
            KingCommandExecutor.access$006(this.this$0);
            KingCubeBuilder.sendCountdownTitle(KingCommandExecutor.access$000(this.this$0) == 0 ? KingCommandExecutor.access$200(this.this$0) : ChatColor.RED + "" + KingCommandExecutor.access$000(this.this$0), this.val$player);
        }

        if (KingCommandExecutor.access$000(this.this$0) <= 0) {
            Location tp = KingCommandExecutor.access$300(this.this$0).clone();
            this.val$player.teleport(tp.add(0.0D, 2.0D, 0.0D));
            int MaxX = tp.getBlockX();
            int MaxZ = tp.getBlockZ();
            int bord = 4;

            for(int x = MaxX - bord; x < MaxX + bord; ++x) {
                for(int z = MaxZ - bord; z < MaxZ + bord; ++z) {
                    KingCubeBuilder.createFireworks(new Location(tp.getWorld(), (double)x, (double)tp.getBlockY(), (double)z), new Color[]{Color.YELLOW, Color.BLUE});
                }
            }

            this.cancel();
            KingCommandExecutor.access$402(this.this$0, (BukkitRunnable)null);
        }

        KingCommandExecutor.access$104(this.this$0);
    }
}