package cc.tianxun.socialnetextra;

import cc.tianxun.socialnetextra.command.*;
import cc.tianxun.socialnetextra.socials.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        // 读取配置文件
        System.out.println("Reading config.");
        saveDefaultConfig();
        // 注册命令/事件
        System.out.println("Registering events/commands.");
        PlayerTp playerTp = new PlayerTp();
        Killself killself = new Killself();
        Objects.requireNonNull(getCommand("tpa")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpac")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpde")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpnel")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("killself")).setExecutor(killself);
        getServer().getPluginManager().registerEvents(playerTp,this);
        getServer().getPluginManager().registerEvents(killself,this);
    }

    @Override
    public void onDisable() {
        // 保存配置文件
        System.out.println("Saving config file");
        saveConfig();
    }
}
