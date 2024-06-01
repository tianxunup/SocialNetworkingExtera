package cc.tianxun.socialnetextra;

import cc.tianxun.socialnetextra.command.*;
import cc.tianxun.socialnetextra.socials.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class Main extends JavaPlugin implements Listener {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        // 读取配置文件
        System.out.println("Reading config.");
        this.saveDefaultConfig();  // config.yml
        // 注册命令/事件
        System.out.println("Registering events/commands.");
        PlayerTp playerTp = new PlayerTp();
        Killself killself = new Killself();
        PlayerPrefix playerPrefix = new PlayerPrefix();
        Objects.requireNonNull(getCommand("tpa")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpac")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpde")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpnel")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("killself")).setExecutor(killself);
        Objects.requireNonNull(getCommand("awarded")).setExecutor(playerPrefix);
        Objects.requireNonNull(getCommand("setprefix")).setExecutor(playerPrefix);
        Objects.requireNonNull(getCommand("prefixs")).setExecutor(playerPrefix);
        getServer().getPluginManager().registerEvents(playerTp,this);
        getServer().getPluginManager().registerEvents(killself,this);
        getServer().getPluginManager().registerEvents(this,this);
    }
    @Override
    public void onDisable() {
        // 保存配置文件
        System.out.println("Saving config file");
        saveConfig();
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerUnit unit = PlayerUnit.registerPlayerUnit(event.getPlayer());
        // unit的初始化....
    }
}
