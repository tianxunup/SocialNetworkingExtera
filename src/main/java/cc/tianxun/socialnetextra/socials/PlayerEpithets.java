package cc.tianxun.socialnetextra.socials;

import cc.tianxun.socialnetextra.Main;
import cc.tianxun.socialnetextra.PlayerUnit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerEpithets implements CommandExecutor, Listener {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("awarded")) {
			if (args.length < 2) {
				sender.sendMessage(String.format("§4命令'%s'需要2个参数，而你传入了%d个",label,args.length));
			}
			PlayerUnit unit = PlayerUnit.getPlayerUnit(args[0]);
			if (unit == null) {
				sender.sendMessage(String.format("§4玩家'%s' 不存在",args[0]));
			}
			else {
				unit.awarded(args[1].replace("&","§"));
				sender.sendMessage(String.format("§a成功授予玩家%s 称号'§r%s§r§a'",args[0],args[1]));
			}
		}
		else if (command.getName().equals("delepithet")) {
			if (args.length < 2) {
				sender.sendMessage(String.format("§4命令'%s'需要2个参数，而你传入了%d个",label,args.length));
			}
			PlayerUnit unit = PlayerUnit.getPlayerUnit(args[0]);
			if (unit == null) {
				sender.sendMessage(String.format("§4玩家'%s' 不存在",args[0]));
			}
			else {
				try {
					unit.deleteEpithet(Integer.parseInt(args[1]));
				}
				catch (NumberFormatException e) {
					sender.sendMessage(String.format("无效的参数 '%s'", args[1]));
				}
				sender.sendMessage(String.format("§a成功删除玩家%s 称号'§r%s§r§a'",args[0],args[1]));
			}
		}
		else if (command.getName().equals("awardedme")) {
			if (!Main.getInstance().getConfig().getBoolean("allow_customize_epithet")) {
				sender.sendMessage("§4服务器未启用自定义称号");
				return true;
			}
			PlayerUnit unit;
			if (sender instanceof Player) {
				unit = PlayerUnit.getPlayerUnit((Player) sender);
			}
			else {
				sender.sendMessage("§4Only players can use this command.");
				return true;
			}
			if (args.length < 1) {
				sender.sendMessage(String.format("§4命令'%s'需要1个参数，而你传入了%d个",label,args.length));
			}
			unit.awarded(args[0].replace("&","§"));
			sender.sendMessage(String.format("§a成功授予自己称号'§r%s§r§a'",args[0]));
		}
		else if (command.getName().equals("delmyepithet")) {
			if (!Main.getInstance().getConfig().getBoolean("allow_customize_epithet")) {
				sender.sendMessage("§4服务器未启用自定义称号");
				return true;
			}
			PlayerUnit unit;
			if (sender instanceof Player) {
				unit = PlayerUnit.getPlayerUnit((Player) sender);
			}
			else {
				sender.sendMessage("§4Only players can use this command.");
				return true;
			}
			if (args.length < 1) {
				sender.sendMessage(String.format("§4命令'%s'需要1个参数，而你传入了%d个",label,args.length));
			}
			try {
				unit.deleteEpithet(Integer.parseInt(args[0]));
				sender.sendMessage(String.format("§a成功删除自己称号'§r%s§r§a'",args[0]));
			}
			catch (NumberFormatException ignored) {
				sender.sendMessage(String.format("无效的参数 '%s'", args[0]));
			}
		}
		else if (command.getName().equals("epithets")) {
			if (args.length == 0) {
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
				}
				else {
					sender.sendMessage("§4Only players can use this command.");
					return true;
				}
				List<String> epithetList = PlayerUnit.getPlayerUnit(player).getEpithetList();
				List<Integer> epithetWornList = PlayerUnit.getPlayerUnit(player).getEpithetWornList();
				if (epithetList.isEmpty()) {
					player.sendMessage("§b你当前没有称号哦");
				}
				else {
					StringBuilder message = new StringBuilder("§b你当前拥有如下称号：\n");
					int index = 0;
					for (String epithet : epithetList) {
						message.append(String.format(" §b§n%d§r - %s§r ", index+1, epithet));
						if (epithetWornList.contains(index)) {
							message.append("§c(使用中)§r");
						}
						message.append("\n");
						index += 1;
					}
					player.sendMessage(message.toString());
					player.sendMessage("你可以使用 /setepithet <id> 设置佩戴的称号");
				}
			}
			else if (args.length == 1) {
				// verfiy permissions
				if (!sender.isOp()) {
					sender.sendMessage("§4你没有权限查看其他玩家的称号！");
					return true;
				}
				// execute
				PlayerUnit unit = PlayerUnit.getPlayerUnit(args[0]);
				if (unit == null) {
					sender.sendMessage(String.format("§4玩家'%s' 不存在",args[0]));
				}
				else {
					List<String> epithetList = unit.getEpithetList();
					StringBuilder message = new StringBuilder(String.format("§b玩家%s 当前拥有如下称号：§r\n", unit.getRawPlayer().getName()));
					for (String epithet : epithetList) {
						message.append(String.format("%s§r，", epithet));
					}
					sender.sendMessage(message.toString());
				}
			}
		}
		else if (command.getName().equals("setepithet")) {
			PlayerUnit unit;
			if (sender instanceof Player) {
				unit = PlayerUnit.getPlayerUnit((Player) sender);
			}
			else {
				sender.sendMessage("§4Only players can use this command.");
				return true;
			}

			List<String> epithetList = unit.getEpithetList();
			if (epithetList.isEmpty()) {
				unit.getRawPlayer().sendMessage("§b你当前没有称号哦");
			}
			else {
				for (String rawArg : args) {
					try {
						unit.wearEpithet(Integer.parseInt(rawArg)-1);
					}
					catch (NumberFormatException e) {
						sender.sendMessage(String.format("无效的参数 '%s'", rawArg));
					}
				}
			}
		}
		return true;
	}
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat(String.format("%s§r§7: §r%s", PlayerUnit.getPlayerUnit(event.getPlayer()).getDisplayName(),event.getMessage()));
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(String.format("%s §r§e加入了游戏", PlayerUnit.getPlayerUnit(event.getPlayer()).getDisplayName()));
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(String.format("%s §r§e退出了游戏", PlayerUnit.getPlayerUnit(event.getPlayer()).getDisplayName()));
	}
}

