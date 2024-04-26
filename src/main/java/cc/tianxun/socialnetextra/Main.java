package cc.tianxun.socialnetextra;

import cc.tianxun.socialnetextra.command.*;
import cc.tianxun.socialnetextra.socials.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static List<String> languageList = new ArrayList<>();
    public static final String defaultLanguage = "en_us";
    private final Map<String,String> langkeys = new HashMap<>();
    static {
        languageList.add("en_us");
//        languageList.add("zh_cn");
//        languageList.add("zh_hans");
    }
    public static Main getInstance() {
        return instance;
    }
    public String getLangKey(String key,String lang) {
        String locateKey = String.format("%s.%s",lang,key);
        if (this.langkeys.get(locateKey) != null) {
            return this.langkeys.get(locateKey);
        }
        else if (this.langkeys.get(key) != null) {
            return this.langkeys.get(key);
        }
        else {
            return key;
        }
    }
    public String getLangKey(String key) {
        if (this.langkeys.get(key) != null) {
            return this.langkeys.get(key);
        }
        else {
            return key;
        }
    }
    @Override
    public void onEnable() {
        instance = this;
        // 读取配置文件
        System.out.println("Reading config.");
        saveDefaultConfig();
        // 读取语言文件
        System.out.println("Reading language files.");
        for (String lang : languageList) {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(String.format("language/%s.lang", lang));
	        BufferedReader buf;
	        if (stream != null) {
		        buf = new BufferedReader(new InputStreamReader(stream));
	        }
            else {
                System.err.printf("Couldn't find language file '%s.lang'%n", lang);
                break;
            }
	        int whichline = 1;
            while (true) {
                String content;
	            try {
                    content=buf.readLine();
	            } catch (IOException e) {
		            throw new RuntimeException(e);
	            }
                if (content == null) {
                    break;
                }
                else if (content.isEmpty()) {
                    continue;
                }
                String[] kv = content.split("=",2);
                if (kv.length != 2) {
                    System.err.println("A language file with wrong in line "+whichline);
                }
                else {
                    this.langkeys.put(String.format("%s.%s",lang,kv[0]),kv[1]);
                    if (lang.equals(defaultLanguage)) {
                        this.langkeys.put(kv[0],kv[1]);
                    }
                }
	            whichline++;
            }
        }
        // 注册命令/事件
        System.out.println("Registering events/commands.");
        PlayerTp playerTp = new PlayerTp();
        Killself killself = new Killself();
        Objects.requireNonNull(getCommand("tpa")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpac")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpde")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("tpnel")).setExecutor(playerTp);
        Objects.requireNonNull(getCommand("kill")).setExecutor(killself);
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
