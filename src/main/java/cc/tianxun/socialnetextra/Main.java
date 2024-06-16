package cc.tianxun.socialnetextra;

import cc.tianxun.socialnetextra.command.*;
import cc.tianxun.socialnetextra.socials.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin implements Listener {
    private static Main instance;
    private YamlConfiguration dataFile;
    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        // 读取配置文件
        System.out.println("Reading config.");
        this.saveDefaultConfig();  // config.yml
        this.saveResource("data.yml",false);
        this.dataFile = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(),"data.yml"));
        // 注册命令/事件
        System.out.println("Registering events/commands.");
        PlayerTp playerTp = new PlayerTp();
        Killself killself = new Killself();
        PlayerEpithets playerEpithets = new PlayerEpithets();
        Objects.requireNonNull(getCommand("tpa")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpac")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpde")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpnel")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("killself")).setExecutor(killself);
        Objects.requireNonNull(getCommand("awarded")).setExecutor(playerEpithets);
        Objects.requireNonNull(getCommand("setepithet")).setExecutor(playerEpithets);
        Objects.requireNonNull(getCommand("epithets")).setExecutor(playerEpithets);
        getServer().getPluginManager().registerEvents(playerTp,this);
        getServer().getPluginManager().registerEvents(killself,this);
        getServer().getPluginManager().registerEvents(this,this);
    }
    @Override
    public void onDisable() {
        // 保存配置文件
        System.out.println("Saving config file");
        saveConfig();
	    try {
		    this.dataFile.save(new File(this.getDataFolder(),"data.yml"));
	    } catch (IOException e) {
		    throw new RuntimeException(e);
	    }
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerUnit unit = PlayerUnit.registerPlayerUnit(event.getPlayer());
        // unit的初始化....
        unit.setRegisterStamp(this.dataFile.getLong(String.format("%s.register_stamp",event.getPlayer().getName())));
        unit.setLastLoginStamp(System.currentTimeMillis());
        this.dataFile.set(String.format("%s.last_login_stamp",event.getPlayer().getName()),unit.getLastLoginStamp());
        unit.setPasswordHash(this.dataFile.getInt(String.format("%s.password_hash",event.getPlayer().getName())));
        for (String epithet : this.dataFile.getStringList(String.format("%s.epithets",event.getPlayer().getName()))) {
            unit.addEpithetx(epithet);
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerUnit.revokedPlayerUnit(event.getPlayer());
    }
}
