package remagdbr.org.mine.reputation;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReputationManager {

    private final HashMap<UUID, Integer> reputacoes = new HashMap<>();
    private final HashMap<UUID, String> faixaAtual = new HashMap<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    private final File file;
    private final FileConfiguration config;

    public ReputationManager() {
        file = new File(Reputation.getInstance().getDataFolder(), "reputacao.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        carregarDados();
    }

    public void salvarDados() {
        try {
            for (Map.Entry<UUID, Integer> entry : reputacoes.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarDados() {
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int valor = config.getInt(key);
            reputacoes.put(uuid, valor);
        }
    }

    public int get(UUID id) {
        return reputacoes.getOrDefault(id, 0);
    }

    public void set(UUID id, int valor) {
        reputacoes.put(id, valor);
        salvarDados();
        Player player = Bukkit.getPlayer(id);
        if (player != null) checkFaixa(player);
    }

    public void add(UUID id, int valor) {
        set(id, get(id) + valor);
    }

    public String getReputationStatus(UUID id) {
        int rep = get(id);
        if (rep >= 50) return "§aHerói";
        if (rep >= 10) return "§2Bondoso";
        if (rep >= -9) return "§7Neutro";
        if (rep >= -49) return "§cCruel";
        return "§4Vilão";
    }

    public String getReputationStatusRaw(UUID id) {
        return getFaixa(get(id));
    }

    public String getFaixa(int valor) {
        if (valor >= 50) return "HEROI";
        if (valor >= 10) return "BONDOSO";
        if (valor >= -9) return "NEUTRO";
        if (valor >= -49) return "CRUEL";
        return "VILAO";
    }

    public void checkFaixa(Player player) {
        UUID id = player.getUniqueId();
        String atual = getFaixa(get(id));
        String antiga = faixaAtual.getOrDefault(id, "");

        if (!atual.equals(antiga)) {
            faixaAtual.put(id, atual);
            ReputationEffects.mostrarMudancaDeFaixa(player, atual);
        }
    }

    public void atualizarScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("reputacao", "dummy", "§6Reputação");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        String status = getReputationStatus(player.getUniqueId());
        int rep = get(player.getUniqueId());

        obj.getScore("§f" + status).setScore(rep);
        player.setScoreboard(board);
    }

    public void showHud(Player player) {
        int rep = get(player.getUniqueId());
        String status = getReputationStatus(player.getUniqueId());
        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent("Reputação: " + status + " §7(" + rep + ")")
        );
    }

    public void createOrUpdateBossBar(Player player) {
        UUID id = player.getUniqueId();
        String faixa = getReputationStatus(player.getUniqueId());
        int rep = get(id);
        String text = "§e[Reputação] " + faixa + " §7(" + rep + ")";

        BossBar bar = bossBars.get(id);
        if (bar == null) {
            bar = Bukkit.createBossBar(text, BarColor.PURPLE, BarStyle.SEGMENTED_10);
            bar.addPlayer(player);
            bossBars.put(id, bar);
        } else {
            bar.setTitle(text);
        }
        bar.setProgress(1.0);
    }

    public void removeBossBar(Player player) {
        UUID id = player.getUniqueId();
        BossBar bar = bossBars.remove(id);
        if (bar != null) bar.removeAll();
    }
}
