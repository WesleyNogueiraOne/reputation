package remagdbr.org.mine.reputation;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class ReputationTask implements Runnable {

    private final ReputationManager manager;
    private final Plugin plugin;

    public ReputationTask(ReputationManager manager, Plugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, this, 20L * 10, 20L * 5); // a cada 5s
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            String faixa = manager.getReputationStatusRaw(id);

            // Atualiza bossbar fixa
            manager.createOrUpdateBossBar(player);

            if (Math.random() < Reputation.reputationEventChance) {
                switch (faixa) {
                    case "HEROI" -> {
                        player.sendTitle("§bLuz te envolve", "§7Você inspira esperança.", 10, 60, 10);
                        player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 30, 0.5, 1, 0.5);
                        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.2f);
                    }
                    case "VILAO" -> {
                        player.sendTitle("§4⚠ Vozes sussurram seu nome...", "§7Você sente o peso de suas ações.", 10, 60, 10);
                        player.spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 30, 0.5, 1, 0.5);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 0.6f);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                        player.setPlayerTime(18000, false);
                    }
                    case "CRUEL" -> {
                        player.sendTitle("§cCulpa sufoca sua alma", "", 10, 40, 10);
                        player.spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation(), 15, 0.4, 1, 0.4);
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1f, 0.8f);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 1));
                    }
                    case "BONDOSO" -> {
                        player.sendTitle("§aVocê age com bondade", "§7As pessoas notam isso.", 10, 60, 10);
                        player.spawnParticle(Particle.HEART, player.getLocation(), 15, 0.5, 1, 0.5);
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.2f);
                    }
                    case "NEUTRO" -> {
                        player.sendTitle("§7Dúvidas rondam sua mente", "", 10, 40, 10);
                        player.spawnParticle(Particle.ASH, player.getLocation(), 15, 0.5, 1, 0.5);
                        player.playSound(player.getLocation(), Sound.BLOCK_BASALT_HIT, 1f, 0.9f);
                    }
                }
            }
        }
    }
}
