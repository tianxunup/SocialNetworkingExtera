package cc.tianxun.socialnetextra.socials;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import cc.tianxun.socialnetextra.Main;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTp implements CommandExecutor, Listener {
	private static final long teleportWaitTicks = 3*20;
	private static final long autoCannelTicks = 3*60*20;
	public static final Map<String, List<String>> tpRequestsQueue = new HashMap<>();
	public static final Map<String,String> willTps = new HashMap<>();
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		tpRequestsQueue.put(event.getPlayer().getName(),new ArrayList<>());
		willTps.put(event.getPlayer().getName(),null);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		tpRequestsQueue.remove(event.getPlayer().getName());
		willTps.remove(event.getPlayer().getName());
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§4Only players can use this command.");
			return true;
		}
		Player player = (Player)sender;
		if (!Main.getInstance().getConfig().getBoolean("enable_tpa")) {
			player.sendMessage(String.format("§4命令'%s'已被服务器禁用", label));
			return true;
		}

		if (command.getName().equals("tpa")) {
			if (args.length != 1) {
				player.sendMessage(
					String.format("§4命令'tpa'需要1个参数，而你传入了%d个",args.length)
				);
				return true;
			}
			for (Player teleportee : Bukkit.getServer().getOnlinePlayers()) {
				if (teleportee.getName().equals(args[0])) {
					tpRequestsQueue.get(teleportee.getName()).add(player.getName());
//					willTps.replace(player.getName(),lltpedPlayer.getName());
					teleportee.sendMessage(String.format("§a玩家 §l§n%s §r§a请求传送到你这", player.getName()));
					teleportee.sendMessage("§a输入'/tpac'接受TA的请求，输入'/tpde'拒绝");
					new CannelTeleportThread(player,teleportee).runTaskLater(Main.getInstance(),autoCannelTicks);
					return true;
				}
			}
			player.sendMessage(
				String.format("§4玩家'%s'不存在",args[0])
			);
		}
		else if (command.getName().equals("tpac")) {
			for (String teleporterName : tpRequestsQueue.get(player.getName())) {
				Player teleporter = Bukkit.getServer().getPlayer(teleporterName);
				if (teleporter != null) {
					new TeleportThread(teleporter,player).runTaskLater(Main.getInstance(),teleportWaitTicks);
					willTps.replace(teleporterName,player.getName());
					teleporter.sendMessage(String.format("§a%s 同意了你的传送请求，即将传送", player.getName()));
					player.sendMessage(String.format("§a成功同意 %s 的传送请求！", teleporterName));
				}
			}
			tpRequestsQueue.get(player.getName()).clear();
		}
		else if (command.getName().equals("tpde")) {
			for (String teleporterName : tpRequestsQueue.get(player.getName())) {
				Player teleporter = Bukkit.getServer().getPlayer(teleporterName);
				if (teleporter != null) {
					willTps.replace(teleporterName,null);
					teleporter.sendMessage(String.format("§2嗯..你的请求被 %s 拒绝了呢...", player.getName()));
					player.sendMessage(String.format("§a成功拒绝 %s 的传送请求！", teleporterName));
				}
			}
			tpRequestsQueue.get(player.getName()).clear();
		}
		else if (command.getName().equals("tpnel")) {
			for (Player teleportee : Bukkit.getServer().getOnlinePlayers()) {
				tpRequestsQueue.get(teleportee.getName()).remove(player.getName());
				teleportee.sendMessage(String.format("§2%s又取消了传送请求呢...", player.getName()));
				player.sendMessage(String.format("§a成功取消了 %s 的传送请求！", teleportee.getName()));
			}
		}
		return true;
	}
}

class CannelTeleportThread extends BukkitRunnable {
	private final Player teleporter;
	private final Player teleportee;
	public CannelTeleportThread(Player teleporter, Player teleportee) {
		this.teleporter = teleporter;
		this.teleportee = teleportee;
	}
	@Override
	public void run() {
		if (PlayerTp.tpRequestsQueue.get(this.teleportee.getName()).remove(this.teleporter.getName())) {
			this.teleporter.sendMessage("§4传送请求已被取消");
			this.teleportee.sendMessage("§4传送请求已被取消");
		}
	}
}

class TeleportThread extends BukkitRunnable {
	private final Player teleporter;
	private final Player teleportee;
	public TeleportThread(Player teleporter, Player teleportee) {
		this.teleporter = teleporter;
		this.teleportee = teleportee;
	}
	@Override
	public void run() {
		if (PlayerTp.willTps.get(this.teleporter.getName()).equals(this.teleportee.getName())) {
			this.teleporter.teleport(this.teleportee);
			this.teleporter.sendMessage("§a传送成功,欢迎相遇！");
			this.teleportee.sendMessage("§a传送成功,欢迎相遇！");
		}
	}
}
