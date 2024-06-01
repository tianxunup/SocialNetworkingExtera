package cc.tianxun.socialnetextra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUnit {
	private static final Map<Player,PlayerUnit> units = new HashMap<>();
	private Player rawPlayer;
	public static PlayerUnit getPlayerUnit(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) {
			return null;
		}
		PlayerUnit unit = units.get(player);
		if (unit == null) {
			System.err.printf("We couldn't find the PlayerUnit as the server player '%s'.%n",playerName);
		}
		return unit;
	}
	public static PlayerUnit getPlayerUnit(Player player){
		PlayerUnit unit = units.get(player);
		if (unit == null) {
			System.err.printf("We couldn't find the PlayerUnit as the server player '%s'.%n",player.getName());
		}
		return unit;
	}
	public static PlayerUnit getPlayerUnit(UUID uuid){
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) {
			return null;
		}
		PlayerUnit unit = units.get(player);
		if (unit == null) {
			System.err.printf("We couldn't find the PlayerUnit as the server player '%s'.%n",player.getName());
		}
		return unit;
	}
	public static PlayerUnit registerPlayerUnit(Player player) {
		if (units.containsKey(player)) {
			Main.getInstance().getLogger().warning(String.format("There was a PlayerUnit as the player '%s' in the map.",player.getName()));
			return units.get(player);
		}
		PlayerUnit unit = new PlayerUnit(player);
		units.put(player,unit);
		return unit;
	}
	PlayerUnit(Player player) {
		this.rawPlayer = player;
	}
}