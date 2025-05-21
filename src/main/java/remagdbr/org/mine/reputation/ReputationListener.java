package remagdbr.org.mine.reputation;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ReputationListener implements Listener {

    private final ReputationManager manager;

    public ReputationListener(ReputationManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = (Player) event.getEntity().getKiller();
            EntityType tipo = event.getEntityType();

            if (tipo == EntityType.PLAYER || tipo == EntityType.VILLAGER) {
                manager.add(killer.getUniqueId(), -10);
                killer.sendMessage("§cVocê perdeu reputação por matar um " + tipo.name().toLowerCase());
                manager.showHud(killer);
                manager.atualizarScoreboard(killer);
            }
        }
    }

    @EventHandler
    public void onTrade(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            Player player = event.getPlayer();
            manager.add(player.getUniqueId(), 5);
            player.sendMessage("§aVocê ganhou reputação por interagir com um aldeão.");
            manager.showHud(player);
            manager.atualizarScoreboard(player);
        }
    }

    @EventHandler
    public void aoEntrar(PlayerJoinEvent event) {
        manager.atualizarScoreboard(event.getPlayer());
    }
}
