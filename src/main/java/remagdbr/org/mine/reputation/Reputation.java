package remagdbr.org.mine.reputation;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Reputation extends JavaPlugin {

    private static Reputation instance;
    public ReputationManager manager;
    public static double reputationEventChance = 0.05;

    @Override
    public void onEnable() {
        instance = this;
        this.manager = new ReputationManager();
        Bukkit.getPluginManager().registerEvents(new ReputationListener(manager), this);
        new ReputationTask(manager, this);
        getLogger().info("Plugin de Reputação ativado!");
    }

    public static Reputation getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar esse comando.");
            return true;
        }

        // Admin: /reputacao set|add|remove <jogador> <valor>
        if (args.length >= 3 && sender.hasPermission("reputation.admin")) {
            String sub = args[0];
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cJogador não encontrado.");
                return true;
            }

            int valor;
            try {
                valor = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cValor inválido.");
                return true;
            }

            UUID targetId = target.getUniqueId();

            switch (sub.toLowerCase()) {
                case "add" -> {
                    manager.add(targetId, valor);
                    sender.sendMessage("§aReputação de " + target.getName() + " aumentada em " + valor);
                }
                case "remove" -> {
                    manager.add(targetId, -valor);
                    sender.sendMessage("§cReputação de " + target.getName() + " reduzida em " + valor);
                }
                case "set" -> {
                    manager.set(targetId, valor);
                    sender.sendMessage("§eReputação de " + target.getName() + " definida como " + valor);
                }
                default -> sender.sendMessage("§cUso: /reputacao [set|add|remove] <jogador> <valor>");
            }
            return true;
        }

        // Comando de chance
        if (args.length == 2 && args[0].equalsIgnoreCase("chance")) {
            if (!player.hasPermission("reputation.config")) {
                player.sendMessage("§cVocê não tem permissão para isso.");
                return true;
            }

            try {
                double novaChance = Double.parseDouble(args[1]);
                if (novaChance < 0 || novaChance > 1) {
                    player.sendMessage("§cA chance deve estar entre 0.0 e 1.0");
                    return true;
                }
                reputationEventChance = novaChance;
                player.sendMessage("§aChance de evento alterada para " + novaChance);
            } catch (NumberFormatException e) {
                player.sendMessage("§cValor inválido.");
            }
            return true;
        }

        // Comando padrão: mostrar reputação
        if (cmd.getName().equalsIgnoreCase("reputacao")) {
            int rep = manager.get(player.getUniqueId());
            String status = manager.getReputationStatus(player.getUniqueId());
            player.sendMessage("§eSua reputação atual: " + status + " §e(" + rep + ")");
            return true;
        }

        return false;
    }
}
