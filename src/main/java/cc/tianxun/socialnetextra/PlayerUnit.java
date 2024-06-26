package cc.tianxun.socialnetextra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerUnit {
	private static final Map<Player,PlayerUnit> units = new HashMap<>();
	private static final List<TeleportThread> teleportThreadList = new ArrayList<>();
	private static final long teleportWaitTicks = 3*20;
	private static final long autocancelTicks = 3*60*20;

	// Player Data
	private final Player rawPlayer;
	private long registerStamp;
	private long lastLoginStamp;
	private int passwordHash;
	private final List<String> epithetList = new ArrayList<>();
	private final List<Integer> epithetWornList = new ArrayList<>();

	// Server Data
	private final List<PlayerUnit> tpRequestsQuene = new ArrayList<>();
	private String displayName;

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
		if (player!=null) {
			revokedPlayerUnit(getPlayerUnit(player));
		}
	}
	public static void revokedPlayerUnit(PlayerUnit unit) {
		if (!units.containsValue(unit)) {
			Main.getInstance().getLogger().warning(String.format("Player '%s' didn't register.", unit.getDisplayName()));
		}
		Main.getInstance().getData().set(String.format("%s.register_stamp", unit.getRawPlayer().getName()),unit.getRegisterStamp());
		Main.getInstance().getData().set(String.format("%s.password_hash", unit.getRawPlayer().getName()),unit.passwordHash);
		Main.getInstance().getData().set(String.format("%s.epithets", unit.getRawPlayer().getName()),unit.epithetList);
		Main.getInstance().getData().set(String.format("%s.epithets_worn", unit.getRawPlayer().getName()),unit.epithetWornList);
		units.remove(unit.getRawPlayer());
	}

	private PlayerUnit(Player player) {
		this.rawPlayer = player;
		this.updateDisplayName();
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

	public void addEpithet(String epithet) {
		this.epithetList.add(epithet);
	}
	
	// tp
	public int addTpRequestFrom(PlayerUnit unit) {
		if (this.tpRequestsQuene.contains(unit)) {
			return 1;  // Player requested.
		}
		this.tpRequestsQuene.add(unit);
		this.getRawPlayer().sendMessage(String.format("§a玩家 §l§n%s §r§a请求传送到你这", unit.getDisplayName()));
		this.getRawPlayer().sendMessage("§a输入'/tpac'接受TA的请求，输入'/tpde'拒绝");
		new AutoCancelTeleportRuquestThread(this,unit).runTaskLater(Main.getInstance(),autocancelTicks);
		return 0;
	}
	public void sendTpRequestTo(PlayerUnit unit) {
		int status = unit.addTpRequestFrom(this);
		if (status == 0) {
			this.getRawPlayer().sendMessage("§a成功发送传送请求！");
		}
		else if (status == 1) {
			this.getRawPlayer().sendMessage("§4您好像已经请求过TA了呢，等一等吧...");
		}
	}
	public void acceptAllTeleportRequests() {
		for (PlayerUnit unit : this.tpRequestsQuene) {
			TeleportThread thread = new TeleportThread(unit,this);
			teleportThreadList.add(thread);
			thread.runTaskLater(Main.getInstance(),teleportWaitTicks);
			unit.getRawPlayer().sendMessage(String.format("§a%s 同意了你的传送请求，即将传送", this.getDisplayName()));
			this.getRawPlayer().sendMessage(String.format("§a成功同意 %s 的传送请求！", unit.getDisplayName()));
		}
		if (this.tpRequestsQuene.isEmpty()) {
			this.getRawPlayer().sendMessage("§4没有人向你发送传送请求哦");
		}
		else {
			this.tpRequestsQuene.clear();
		}
	}
	public void acceptTeleportRequestFrom(PlayerUnit unit) {
		if (this.tpRequestsQuene.contains(unit)) {
			this.tpRequestsQuene.remove(unit);
			TeleportThread thread = new TeleportThread(unit,this);
			teleportThreadList.add(thread);
			thread.runTaskLater(Main.getInstance(),teleportWaitTicks);
			unit.getRawPlayer().sendMessage(String.format("§a%s 同意了你的传送请求，即将传送", this.getDisplayName()));
			this.getRawPlayer().sendMessage(String.format("§a成功同意 %s 的传送请求！", unit.getDisplayName()));
		}
		else {
			this.getRawPlayer().sendMessage(String.format("§4%s 尚未向你发送传送请求", unit.getDisplayName()));
		}
	}
	public void denyAllTeleportRequests() {
		for (PlayerUnit unit : this.tpRequestsQuene) {
			unit.getRawPlayer().sendMessage(String.format("§2嗯..你的请求被 %s 拒绝了呢...", this.getDisplayName()));
			this.getRawPlayer().sendMessage(String.format("§a成功拒绝 %s 的传送请求！", unit.getDisplayName()));
		}
		if (this.tpRequestsQuene.isEmpty()) {
			this.getRawPlayer().sendMessage("§4没有人向你发送传送请求哦");
		}
		else {
			this.tpRequestsQuene.clear();
		}
	}
	public void denyTeleportRequestFrom(PlayerUnit unit) {
		if (this.tpRequestsQuene.contains(unit)) {
			this.tpRequestsQuene.remove(unit);
			unit.getRawPlayer().sendMessage(String.format("§2嗯..你的请求被 %s 拒绝了呢...", this.getDisplayName()));
			this.getRawPlayer().sendMessage(String.format("§a成功拒绝 %s 的传送请求！", unit.getDisplayName()));
		}
		else {
			this.getRawPlayer().sendMessage(String.format("§4%s 尚未向你发送传送请求", unit.getDisplayName()));
		}
	}
	public void cancelAllTeleportRequests() {
		for (TeleportThread thread : teleportThreadList) {
			if (this.equals(thread.getTeleporter())) {
				this.getRawPlayer().sendMessage(String.format("§a成功取消了 %s 的传送请求！", thread.getTeleportee().getDisplayName()));
				thread.getTeleportee().getRawPlayer().sendMessage(String.format("§2%s又取消了传送请求呢...", this.getDisplayName()));
			}
			else if (this.equals(thread.getTeleportee())) {
				this.getRawPlayer().sendMessage(String.format("§a成功取消了 %s 的传送请求！", thread.getTeleporter().getDisplayName()));
				thread.getTeleporter().getRawPlayer().sendMessage(String.format("§2%s又取消了传送请求呢...", this.getDisplayName()));
			}
			thread.cancel();
		}
		teleportThreadList.clear();
		this.getRawPlayer().sendMessage("§4你还没有传送任务呢");
	}
	public void cancelTeleportRequestWith(PlayerUnit unit) {
		for (TeleportThread thread : teleportThreadList) {
			if (
				(this.equals(thread.getTeleporter()) && unit.equals(thread.getTeleportee())) ||
					(this.equals(thread.getTeleportee()) && unit.equals(thread.getTeleporter()))
			) {
				thread.cancel();
				this.getRawPlayer().sendMessage(String.format("§a成功取消了 %s 的传送请求！", unit.getDisplayName()));
				unit.getRawPlayer().sendMessage(String.format("§2%s又取消了传送请求呢...", this.getDisplayName()));
				teleportThreadList.remove(thread);
				return;
			}
		}
		this.getRawPlayer().sendMessage(String.format("§4%s 和您之间没有传送请求哦", unit.getDisplayName()));
	}
	public void cancelTeleportRequestForTimeOut(PlayerUnit unit) {
		for (TeleportThread thread : teleportThreadList) {
			if (
				(this.equals(thread.getTeleporter()) && unit.equals(thread.getTeleportee())) ||
					(this.equals(thread.getTeleportee()) && unit.equals(thread.getTeleporter()))
			) {
				thread.cancel();
				this.getRawPlayer().sendMessage(String.format("§a取消与 %s 的传送请求！", unit.getDisplayName()));
				unit.getRawPlayer().sendMessage(String.format("§a取消与 %s 的传送请求！", this.getDisplayName()));
				teleportThreadList.remove(thread);
				return;
			}
		}
	}

	public void awarded(String epithet) {
		this.epithetList.add(epithet);
	}
	public void wearEpithet(int id) {
		if (id < this.epithetList.size() && id >= 0) {
			this.epithetWornList.add(id);
			if (this.epithetWornList.size() > Main.getInstance().getConfig().getInt("max_epithets_worn")) {
				this.epithetList.remove(0);
			}
			this.getRawPlayer().sendMessage(String.format("§a成功佩戴称号 '§r%s§r§a'", this.epithetList.get(id)));
		}
		else {
			this.getRawPlayer().sendMessage(String.format("§4无效的id: '%d'", id+1));
		}
		this.updateDisplayName();
	}
	public void addWornEpithet(int id) {
		if (id < this.epithetList.size() && id >= 0) {
			this.epithetWornList.add(id);
			if (this.epithetWornList.size() > Main.getInstance().getConfig().getInt("max_epithets_worn")) {
				this.epithetList.remove(0);
			}
		}
		this.updateDisplayName();
	}
	public List<Integer> getEpithetWornList() {
		return new ArrayList<>(this.epithetWornList);
	}
	public void deleteEpithet(int id) {
		this.epithetWornList.remove(new Integer(id));
		int i=0;
		for (Integer epithetId : this.epithetWornList) {
			if (epithetId > id) {
				this.epithetWornList.set(i,epithetId-1);	
			}
			i++;
		}
		this.epithetList.remove(id);
		this.updateDisplayName();
	}
	public List<String> getEpithetList() {
		return new ArrayList<>(this.epithetList);
	}

	private void updateDisplayName() {
		StringBuilder builder = new StringBuilder();
		for (Integer epithetId : this.epithetWornList) {
			builder.append(String.format("§r§7[%s§r§7]", this.epithetList.get(epithetId)));
		}
		if (!this.epithetList.isEmpty()) {
			builder.append(" ");
		}
		builder.append(String.format("§r%s§r", this.getRawPlayer().getName()));
		this.displayName = builder.toString();
		this.getRawPlayer().setCustomName(displayName);
		this.getRawPlayer().setDisplayName(displayName);
		this.getRawPlayer().setPlayerListName(displayName);
	}
	public String getDisplayName() {
		return this.displayName;
	}
}

class TeleportThread extends BukkitRunnable {
	private final PlayerUnit teleporter;
	private final PlayerUnit teleportee;
	public TeleportThread(PlayerUnit teleporter, PlayerUnit teleportee) {
		this.teleporter = teleporter;
		this.teleportee = teleportee;
	}

	public PlayerUnit getTeleporter() {
		return this.teleporter;
	}
	public PlayerUnit getTeleportee() {
		return this.teleportee;
	}

	@Override
	public void run() {
		this.teleporter.getRawPlayer().teleport(this.teleportee.getRawPlayer());
	}
}

class AutoCancelTeleportRuquestThread extends BukkitRunnable {
	private final PlayerUnit teleporter;
	private final PlayerUnit teleportee;

	public AutoCancelTeleportRuquestThread(PlayerUnit teleporter, PlayerUnit teleportee) {
		this.teleporter = teleporter;
		this.teleportee = teleportee;
	}

	@Override
	public void run() {
		this.teleporter.cancelTeleportRequestForTimeOut(this.teleportee);
	}
}