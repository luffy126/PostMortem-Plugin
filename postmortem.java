package vpm.postmortem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class postmortem extends JavaPlugin implements Listener {
    public static String prefijo = "DobleVida";
    // Mapa para rastrear si un jugador tiene su segunda vida
    private final HashMap<UUID, Boolean> secondLifeMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Registrar eventos
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getConsoleSender().sendMessage("Se activó el plugin 'Doble Vida'");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Inicializar segunda vida si es nuevo
        secondLifeMap.putIfAbsent(playerId, true);
        player.sendMessage(ChatColor.GREEN + "Bienvenido a PostMortem!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        // Verificar si aún tiene su segunda vida
        if (secondLifeMap.getOrDefault(playerId, false)) {
            secondLifeMap.put(playerId, false);
            player.sendMessage(ChatColor.RED + "Has usado tu segunda vida. Ya no tendrás mas oportunidades.");
            // Evitar baneo y revivir al jugador

        } else {
            // Baneo del jugador
            Bukkit.getScheduler().runTask(this, () -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "Dormiste. Gracias por jugar a PostMortem", null, "Servidor");
                player.kickPlayer(ChatColor.RED + "Dormiste. Gracias por jugar a PostMortem.");

                // Mensaje para los demas
                String banMessage = ChatColor.RED + player.getName() + " ha sido baneado del servidor";
                Bukkit.broadcastMessage(banMessage);
            });
        }
    }

    /*
    public void registerCommand() {
        this.getCommand("traspasarvida").setExecutor(new ComandoMain());
    } */

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage("Adios. Plugin by luffy126 :D");
    }

}
