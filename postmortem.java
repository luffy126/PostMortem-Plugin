package vpm.postmortem;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.GameRule;
import org.bukkit.World;

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
    public void onTotemUsed(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.isCancelled() == false) {
                Player player = (Player)event.getEntity();
                String mensaje = ChatColor.GOLD + "" + ChatColor.BOLD + "El jugador " + player.getName() + " ha gastado un tótem.";
                Bukkit.broadcastMessage(mensaje);
            }
        }
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
            String mensajeDeMuerte = "El jugador " + ChatColor.RED + player.getName() + " ha muerto, y ahora está usando su " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "ULTIMA " + ChatColor.RESET + ChatColor.RED + "vida";
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
            }
            Bukkit.broadcastMessage(mensajeDeMuerte);
            actualizarVida(playerId, false);
            secondLifeMap.put(playerId, false);

        } else { // Baneo del jugador
            Bukkit.getScheduler().runTask(this, () -> {

                World world = getServer().getWorld("world");

                // Mensaje en pantalla

                player.setGameMode(GameMode.SPECTATOR);

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    // reproducir sonido
                    onlinePlayer.playSound(onlinePlayer.getLocation(), "custom.death_sound", 1.0F, 1.0F);

                    String title = ChatColor.RED + "¡DORMISTE!";
                    String subtitle = ChatColor.GRAY + "El jugador " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " ha muerto.";
                    onlinePlayer.sendTitle(title, subtitle, 10, 70, 20);
                }

                String banMessage = ChatColor.RED + "" + ChatColor.BOLD + "El oscuro destino de " + player.getName() + " se ha cumplido. \n" + "¡" + ChatColor.RESET + ChatColor.DARK_RED + "" + ChatColor.BOLD + "DURMIÓ " + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "bajo el peso del infierno y las llamas ETERNAS!";
                Bukkit.broadcastMessage(banMessage);

                // El ban
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "Moriste pa. Gracias por jugar a PostMortem!", null, "Servidor");
                    player.kickPlayer(ChatColor.RED + "Dormiste. Gracias por jugar a PostMortem!");
                }, 60L);

                UUID playerID = player.getUniqueId();
                Bukkit.getConsoleSender().sendMessage(playerID + "<- UUID del jugador " + player.getName() + " Anotar por algún lado");
            });
        }
    }

    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage("Adios. Plugin by luffy126 :D");
    }
