package cc.tianxun.socialnetextra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerUnit {
	private static final Map<Player,PlayerUnit> units = new HashMap<>();

	// Player Data
	private final Player rawPlayer;
	private long registerStamp;
	private long lastLoginStamp;
	private int passwordHash;
	private final List<String> prefixList = new ArrayList<>();

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
	public static void revokedPlayerUnit(Player player) {
		if (units.remove(player)==null) {
			Main.getInstance().getLogger().warning(String.format("Player '%s' didn't register.", player.getName()));
		}
	}
	public static void revokedPlayerUnit(PlayerUnit unit) {
		Player player = unit.getRawPlayer();
		if (units.remove(player)==null) {
			Main.getInstance().getLogger().warning(String.format("Player '%s' didn't register.", player.getName()));
		}
	}

	private PlayerUnit(Player player) {
		this.rawPlayer = player;
	}

	public Player getRawPlayer() {
		return rawPlayer;
	}
	public long getLastLoginStamp() {
		return lastLoginStamp;
	}
	public long getRegisterStamp() {
		return registerStamp;
	}

	public void setRegisterStamp(long registerStamp) {
		this.registerStamp = registerStamp;
	}

	public void setLastLoginStamp(long lastLoginStamp) {
		this.lastLoginStamp = lastLoginStamp;
	}

	public void setPassword(String password) {
		this.passwordHash = password.hashCode();
	}
	public void setPasswordHash(int passwordHash) {
		this.passwordHash = passwordHash;
	}
	public boolean verifyPassword(String password) {
		return (password.hashCode() == this.passwordHash);
	}

	public void addPrefix(String prefix) {
		this.prefixList.add(prefix);
	}
}