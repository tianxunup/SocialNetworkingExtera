package cc.tianxun.socialnetextra.command;

import cc.tianxun.socialnetextra.Main;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Killself implements CommandExecutor, Listener {
	public static double killDamage = 32768;
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§4Only players can use this command.");
			return true;
		}
		Player player = (Player)sender;
		if (!Main.getInstance().getConfig().getBoolean("enable_killself")) {
			player.sendMessage(Main.getInstance().getLangKey("command.killself.disabled_message",player.getLocale()));
			return true;
		}

		player.damage(killDamage);
		return true;
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getLastDamage() == killDamage) {
			event.setDeathMessage(event.getEntity().getName() + "自杀了");
		}
	}
}
