package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic;

import io.github.arcaneplugins.blackwidow.lib.cmdblocking.UpdateChecker;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.InvalidObjectException;

public class BukkitVersionChecker {
    public BukkitVersionChecker(final BlackWidow plugin){
        this.plugin = plugin;
    }

    private final BlackWidow plugin;
    private BukkitTask notifyTask;
    boolean logUpdates;
    private boolean notifyPlayers;
    private String notifyMessage;
    private int lastTimerDuration;

    public void load(final boolean isStartup){
        final CommentedConfigurationNode settings = plugin.settings().root()
                .node("update-checker");

        final boolean enabled = settings.node("enabled").getBoolean(true);
        final boolean runOnStartup = settings.node("run-on-startup").getBoolean(true);
        final int repeatTimerDuration = settings.node("repeat-timer-duration-mins").getInt();
        logUpdates = settings.node("log-updates").getBoolean(true);
        notifyPlayers = settings.node("notify-players-with-permission").getBoolean(true);

        if (!enabled){
            disableChecker();
            return;
        }

        startTimerIfNeeded(repeatTimerDuration);

        if (runOnStartup && isStartup){
            getLatestVersion();
        }
    }

    public boolean getNotifyPlayers(){
        return notifyPlayers;
    }

    public String getNotifyMessage(){
        return notifyMessage;
    }

    private void disableChecker(){
        if (notifyTask != null && !notifyTask.isCancelled()){
            notifyTask.cancel();
            notifyTask = null;
        }
    }

    private void startTimerIfNeeded(final int repeatTimerDuration){
        if (repeatTimerDuration <= 0){
            disableChecker();
            return;
        }

        if (notifyTask != null && !notifyTask.isCancelled()
                && repeatTimerDuration == lastTimerDuration){
            return;
        }

        disableChecker();

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getLatestVersion();
            }
        };

        lastTimerDuration = repeatTimerDuration;
        final long delay = repeatTimerDuration * 20L * 60L;
        this.notifyTask = runnable.runTaskTimerAsynchronously(plugin, delay, delay);
    }

    private void getLatestVersion(){
        new BukkitRunnable() {
            @Override
            public void run() {
                checkForLatestVersion();
            }
        }.runTaskAsynchronously(plugin);
    }

    private void checkForLatestVersion(){
        // this function
        final UpdateChecker updateChecker = new UpdateChecker("BlackWidow");

        try{
            final boolean result =  updateChecker.getLatestVersion(latestVersion -> {
                //noinspection deprecation
                final String currentVersion = plugin.getDescription().getVersion()
                        .split(" ")[0];

                if (latestVersion == null && logUpdates){
                    plugin.getLogger().warning("Error check for latest version, string was null");
                    return;
                }

                final VersionInfo thisVersion;
                final VersionInfo hangarVersion;
                boolean isOutOfDate;
                boolean isNewerVersion = false;
                boolean wasUpToDate = false;

                try{
                    thisVersion = new VersionInfo(currentVersion);
                    hangarVersion = new VersionInfo(latestVersion);

                    isOutOfDate = (thisVersion.compareTo(hangarVersion) < 0);
                    isNewerVersion = (thisVersion.compareTo(hangarVersion) > 0);
                }
                catch (InvalidObjectException e){
                    plugin.getLogger().warning("Got exception creating version objects: " + e.getMessage());
                    isOutOfDate = !currentVersion.equals(latestVersion);
                }

                if (isNewerVersion){
                    notifyMessage = "Your BlackWindow version is a pre-release. Latest release version is " + latestVersion + ". (You're running " + currentVersion + ")";
                }
                else if (isOutOfDate){
                    notifyMessage = "Your BlackWidow version is outdated! Please update to " + latestVersion + " as soon as possible. (You're running " + currentVersion + ")";
                }
                else{
                    notifyMessage = "Your BlackWidow version is up to date (You're running " + currentVersion + ")";
                    wasUpToDate = true;
                }

                if (logUpdates){
                    plugin.getLogger().info(notifyMessage);
                }

                if (!wasUpToDate){
                    notifyPlayers();
                }
            });

            if (!result){
                plugin.getLogger().warning("Error getting latest version: " + updateChecker.getErrorMessage());
            }
        }
        catch (Exception e){
            plugin.getLogger().warning("Error getting latest version: " + e.getMessage());
        }
    }

    private void notifyPlayers(){
        if (!notifyPlayers || notifyMessage == null){
            return;
        }

        final String requiredPermission = "blackwidow.notifyupdates";

        for (final Player player : Bukkit.getOnlinePlayers()){
            if (!player.isValid()){
                continue;
            }
            if (!player.hasPermission(requiredPermission)){
                continue;
            }

            player.sendMessage(notifyMessage);
        }
    }
}
