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
	private static final long teleportWaitTicks = 5*20;
	private static final long autoCannelTicks = 3*60*20;
	public static final Map<String, List<String>> tpRequestsQueue = new HashMap<>();
	public static final Map<String,String> willTps = new HashMap<>();
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		tpRequestsQueue.put(event.getPlayer().getName(),new ArrayList<>());
		willTps.put(event.getPlayer().getName(),null);
		System.out.println("The locatle of the player is "+event.getPlayer().getLocale());
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
			player.sendMessage(Main.getInstance().getLangKey("command.tpa.disabled_message",player.getLocale()));
			return true;
		}

		if (command.getName().equals("tpa")) {
			if (args.length != 1) {
				player.sendMessage(
					String.format(Main.getInstance().getLangKey("command.generic.argserror"),"tpa",1,args.length)
				);
				return true;
			}
			for (Player teleportee : Bukkit.getServer().getOnlinePlayers()) {
				if (teleportee.getName().equals(args[0])) {
					tpRequestsQueue.get(teleportee.getName()).add(player.getName());
//					willTps.replace(player.getName(),lltpedPlayer.getName());
					new CannelTeleportThread(player,teleportee).runTaskLater(Main.getInstance(),autoCannelTicks);
					return true;
				}
			}
			player.sendMessage(
				String.format(Main.getInstance().getLangKey("command.generic.playernotfound"),args[0])
			);
		}
		else if (command.getName().equals("tpac")) {
			for (String teleporterName : tpRequestsQueue.get(player.getName())) {
				Player teleporter = Bukkit.getServer().getPlayer(teleporterName);
				if (teleporter != null) {
					new TeleportThread(teleporter,player).runTaskLater(Main.getInstance(),teleportWaitTicks);
					willTps.replace(teleporterName,player.getName());
					teleporter.sendMessage(Main.getInstance().getLangKey("command.tpac.willtp", teleporter.getLocale()));
					player.sendMessage(Main.getInstance().getLangKey("command.tpac.sufceeuly", player.getLocale()));
				}
			}
			tpRequestsQueue.get(player.getName()).clear();
		}
		else if (command.getName().equals("tpde")) {
			for (String teleporterName : tpRequestsQueue.get(player.getName())) {
				Player teleporter = Bukkit.getServer().getPlayer(teleporterName);
				if (teleporter != null) {
					willTps.replace(teleporterName,null);
					teleporter.sendMessage(Main.getInstance().getLangKey("command.tpde.denied", teleporter.getLocale()));
					player.sendMessage(Main.getInstance().getLangKey("command.tpde.sufceeuly", player.getLocale()));
				}
			}
			tpRequestsQueue.get(player.getName()).clear();
		}
		else if (command.getName().equals("tpel")) {
			for (Player teleportee : Bukkit.getServer().getOnlinePlayers()) {
				tpRequestsQueue.get(teleportee.getName()).remove(player.getName());
				teleportee.sendMessage(Main.getInstance().getLangKey("commond.tpa.canneled", teleportee.getLocale()));
			}
			player.sendMessage(Main.getInstance().getLangKey("command.tpel.sufceeuly", player.getLocale()));
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
			this.teleporter.sendMessage(Main.getInstance().getLangKey("commond.tpa.canneled", this.teleporter.getLocale()));
			this.teleportee.sendMessage(Main.getInstance().getLangKey("commond.tpa.canneled", this.teleportee.getLocale()));
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
			this.teleporter.sendMessage(Main.getInstance().getLangKey("commond.tpa.teleported", this.teleporter.getLocale()));
			this.teleportee.sendMessage(Main.getInstance().getLangKey("commond.tpa.teleported", this.teleportee.getLocale()));
		}
	}
}
