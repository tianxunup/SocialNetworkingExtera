package cc.tianxun.socialnetextra.socials;

import cc.tianxun.socialnetextra.PlayerUnit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
				unit.awarded(args[1]);
				sender.sendMessage(String.format("§a成功授予玩家%s 称号'§r%s§r§a'",args[0],args[1]));
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
		StringBuilder message = new StringBuilder();
		PlayerUnit unit = PlayerUnit.getPlayerUnit(event.getPlayer());
		List<String> epithetList = unit.getEpithetList();
		for (Integer epithetId : unit.getEpithetWornList()) {
			message.append(String.format("§r§7[%s§r§7]", epithetList.get(epithetId)));
		}
		if (!epithetList.isEmpty()) {
			message.append(" ");
		}
		message.append(String.format("§r%s§r§7: §r%s", event.getPlayer().getName(),event.getMessage()));
		event.setFormat(message.toString());
	}
}

