package vpm.postmortem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class postmortem extends JavaPlugin implements Listener {
    public static String prefijo = "DobleVida";

    // Mapa para rastrear si un jugador tiene su segunda vida
    private final HashMap<UUID, Boolean> secondLifeMap = new HashMap<>();
    private FileConfiguration vidasConfig;
    private File vidasFile;

    @Override
    public void onEnable() {
        // cargar o crear el archivo de vidas
        vidasFile = new File(getDataFolder(), "vidas.yml");
        if (!vidasFile.exists()) {
            vidasFile.getParentFile().mkdirs();
            saveResource("vidas.yml", false);  // Este método asegura que el archivo se copie si no existe
        }
        vidasConfig = YamlConfiguration.loadConfiguration(vidasFile);

        if (vidasConfig.getConfigurationSection("jugadores") == null) {
            vidasConfig.createSection("jugadores");
            guardarVidas();  // guardar archivo
        }

        // cargar los datos de vidas en el mapa
        for (String uuidStr : vidasConfig.getConfigurationSection("jugadores").getKeys(false)) {
            UUID playerId = UUID.fromString(uuidStr);
            boolean tieneSegundaVida = vidasConfig.getBoolean("jugadores." + uuidStr, true);
            secondLifeMap.put(playerId, tieneSegundaVida);
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void guardarVidas() {
        try {
            vidasConfig.save(vidasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actualizarVida(UUID playerId, boolean tieneSegundaVida) {
        vidasConfig.set("jugadores." + playerId.toString(), tieneSegundaVida);
        guardarVidas();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        player.setGameMode(GameMode.SURVIVAL);

        // Cargar el estado del jugador o asignar como nuevo
        boolean tieneSegundaVida = vidasConfig.getBoolean("jugadores." + playerId.toString(), true);
        secondLifeMap.put(playerId, tieneSegundaVida);
        player.sendMessage(ChatColor.GREEN + "Bienvenido a PostMortem!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        if (secondLifeMap.getOrDefault(playerId, false)) { // Verificar si aún tiene su segunda vida
            actualizarVida(playerId, false);
            secondLifeMap.put(playerId, false);
            player.sendMessage(ChatColor.RED + "Has usado tu segunda vida. Ya no tendrás mas oportunidades.");
            // Evitar baneo y revivir al jugador

        } else { // Baneo del jugador
            Bukkit.getScheduler().runTask(this, () -> {

                // Mensaje en pantalla
                player.setGameMode(GameMode.SPECTATOR);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                player.sendTitle(ChatColor.RED + "Dormiste", ChatColor.GRAY + "no era por ahí :(", 10, 70, 20);

                String banMessage = ChatColor.RED + player.getName() + " ha sido baneado del servidor";
                // El ban
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "Moriste pa. Gracias por jugar a PostMortem!", null, "Servidor");
                    player.kickPlayer(ChatColor.RED + "Dormiste. Gracias por jugar a PostMortem!");

                    // Mensaje al servidor
                    Bukkit.broadcastMessage(banMessage);
                }, 60L);
            });
        }
    }

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage("Adios. Plugin by luffy126 :D");
    }

}
