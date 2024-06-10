package cc.tianxun.socialnetextra.socials;

import cc.tianxun.socialnetextra.PlayerUnit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import cc.tianxun.socialnetextra.Main;
import org.bukkit.event.player.*;

import java.util.*;

public class PlayerTp implements CommandExecutor, Listener {
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
					String.format("§4命令'%s'需要1个参数，而你传入了%d个",label,args.length)
				);
				return true;
			}
			PlayerUnit teleportee = PlayerUnit.getPlayerUnit(args[0]);
			if (teleportee == null) {
				player.sendMessage(
					String.format("§4未找到玩家'%s'",args[0])
				);
				return true;
			}
			PlayerUnit.getPlayerUnit(player).sendTpRequestTo(teleportee);
		}
		else if (command.getName().equals("tpac")) {
			if (args.length == 0) {
				PlayerUnit.getPlayerUnit(player).acceptAllTeleportRequests();
			}
			else {
				for (String playerName : args) {
					PlayerUnit teleporter = PlayerUnit.getPlayerUnit(playerName);
					if (teleporter == null) {
						player.sendMessage(String.format("§4未找到玩家'%s'", playerName));
						continue;
					}
					PlayerUnit.getPlayerUnit(player).acceptTeleportRequestFrom(teleporter);
				}
			}
		}
		else if (command.getName().equals("tpde")) {
			if (args.length == 0) {
				PlayerUnit.getPlayerUnit(player).denyAllTeleportRequests();
			}
			else {
				for (String playerName : args) {
					PlayerUnit teleporter = PlayerUnit.getPlayerUnit(playerName);
					if (teleporter == null) {
						player.sendMessage(String.format("§4未找到玩家'%s'", playerName));
						continue;
					}
					PlayerUnit.getPlayerUnit(player).denyTeleportRequestFrom(teleporter);
				}
			}
		}
		else if (command.getName().equals("tpnel")) {
			if (args.length == 0) {
				PlayerUnit.getPlayerUnit(player).cancelAllTeleportRequests();
			}
			else {
				for (String playerName : args) {
					PlayerUnit teleporter = PlayerUnit.getPlayerUnit(playerName);
					if (teleporter == null) {
						player.sendMessage(String.format("§4未找到玩家'%s'", playerName));
						continue;
					}
					PlayerUnit.getPlayerUnit(player).cancelTeleportRequestWith(teleporter);
				}
			}
		}
		return true;
	}
}