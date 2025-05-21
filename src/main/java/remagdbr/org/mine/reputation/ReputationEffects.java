package remagdbr.org.mine.reputation;

import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReputationEffects {

    public static void mostrarMudancaDeFaixa(Player player, String faixa) {
        switch (faixa) {
            case "HEROI" -> {
                player.sendTitle("§a✦ Uma presença serena...", "§7O mundo te observa com esperança.", 10, 60, 20);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.5f);
                player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 25, 0.5, 1, 0.5);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                mostrarBossBar(player, "§a[HERÓI] Sua presença inspira os outros...", BarColor.GREEN);
            }
            case "VILAO" -> {
                player.sendTitle("§4✖ Vozes ecoam...", "§8Algo te observa na escuridão.", 10, 60, 20);
                player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.6f);
                player.spawnParticle(Particle.SOUL, player.getLocation(), 25, 0.5, 1, 0.5);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
                mostrarBossBar(player, "§4[VILÃO] As trevas te acompanham...", BarColor.RED);
            }
            case "CRUEL" -> {
                player.sendTitle("§cCrueldade o consome.", "§7Você sente algo corromper seu espírito.", 10, 60, 20);
                mostrarBossBar(player, "§c[CRUEL] Você escolheu o caminho da dor...", BarColor.PINK);
            }
            case "BONDOSO" -> {
                player.sendTitle("§bSeu coração é leve.", "§7Outros confiam em você.", 10, 60, 20);
                mostrarBossBar(player, "§b[BONDOSO] Um exemplo de compaixão...", BarColor.BLUE);
            }
            case "NEUTRO" -> {
                player.sendTitle("§7Equilíbrio incerto.", "§8Suas ações moldarão seu destino.", 10, 60, 20);
                mostrarBossBar(player, "§7[NEUTRO] Você ainda pode escolher...", BarColor.WHITE);
            }
        }
    }

    public static void mostrarBossBar(Player player, String texto, BarColor cor) {
        BossBar bar = Bukkit.createBossBar(texto, cor, BarStyle.SEGMENTED_10);
        bar.addPlayer(player);
        bar.setProgress(1.0);
        Bukkit.getScheduler().runTaskLater(Reputation.getInstance(), () -> bar.removePlayer(player), 100L);
    }
}
