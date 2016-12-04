package ru.blayzer.XPSystem;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class LevelHandler implements Listener {
    private Plugin plugin;

    public LevelHandler(Plugin pluginIn) {
        plugin = pluginIn;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onXpGain(PlayerExpChangeEvent event) {

        // Берем массив уровней из конфига
        ArrayList<String> levelList = new ArrayList<>(plugin.getConfig().getStringList("levels"));

        // Берем значения из массива и изменяем систему уровня
        if (!levelList.get(0).equals("none")) {
            Player player = event.getPlayer();
            for (String s : levelList) {

            	int levelCompare = Integer.parseInt(s.split(" ")[0] )- 1;
            	int xpAmount = Integer.parseInt(s.split(" ")[1]);
                int playerLvl = player.getLevel();
                
                if (playerLvl == levelCompare) {
                    float xpBarCurrent = player.getExp() * xpAmount;
                    float xpBarNew = (xpBarCurrent + event.getAmount()) / xpAmount;
                    event.setAmount(0);
                    player.setExp(xpBarNew);
                    return;
                }
                }
            
            // Если игрок достигает самого высокого уровня - пишем это в чат
            if (plugin.getConfig().getBoolean("messages.msgmaxlvl") != false) {
            player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.maxlvl"));
            }
            
            event.setAmount(0);
            }
        }
    
    // Изменяем дроп количества опыта с бутылка с опытом
	@EventHandler (priority = EventPriority.MONITOR)
	public void onExpBottleEvent(ExpBottleEvent e) {
		
        if (plugin.getConfig().getInt("bottle.xp") != 0) {
		e.setExperience(plugin.getConfig().getInt("bottle.xp"));
        }
	}
    
	// Когда игрок повышает свой уровень
	@EventHandler (priority = EventPriority.MONITOR)
	public void onLevelUp(PlayerLevelChangeEvent e) {
		Player player = e.getPlayer();
		
		//Пишем в чат сообщения и проигрываем звук
		if (plugin.getConfig().getBoolean("sounds.msglvlup") != false) {
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.3F, 0);
		}
        if (plugin.getConfig().getBoolean("messages.msglvlup") != false) {
        player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.lvlup") + e.getNewLevel() + "!");
        }
        // Исполняем функцию добавления дополнительных сердец
        scaleHealth(player);
	}
	
    // Выключаем потерю уровня и опыта при смерти
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (plugin.getConfig().getBoolean("death.savelvl") != false) {
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		}
	}
	
	// Изменяем требования к уровню в пвп
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent e){
	    if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
	        Player attacked = (Player)e.getEntity();
	        Player attacker = (Player)e.getDamager();
	        
        	// Требуемый уровень для ПвП
    		if (plugin.getConfig().getBoolean("pvp.funcreqpvplvl") != false) {
            	if(!(attacker.getLevel() >= plugin.getConfig().getInt("pvp.reqpvplvl") && attacked.getLevel() >= plugin.getConfig().getInt("pvp.reqlvl"))) {
                  e.setCancelled(true);
            	if (plugin.getConfig().getBoolean("messages.msgreqlvl") != false) {
            		attacker.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.reqlvl") + plugin.getConfig().getInt("pvp.reqlvl") + "!");
                }
                }
            }
	        
	       // Разница в уровне между игроками для ПвП
	        if (plugin.getConfig().getBoolean("pvp.funcdiffpvplvl") != false) {
	            int lvl = Math.abs(attacker.getLevel() - attacked.getLevel());
	        if(lvl > plugin.getConfig().getInt("pvp.diffpvplvl")) {
	            e.setCancelled(true);
                if (plugin.getConfig().getBoolean("messages.msgreqlvl") != false) {
                	attacked.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.reqlvldmg"));
                	attacker.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.reqlvldmg"));
                }
	        }
	        }
	    }
	}
	
	
	// При заходе игрока, вызывать функцию добавления и проверки сердец
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
        scaleHealth(player);
    }
    
    //Первый вход игрока
    @EventHandler (priority = EventPriority.MONITOR)
    public void PlayerJoin(PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	
    if(!player.hasPlayedBefore()) {
     if (plugin.getConfig().getBoolean("firstjoin.firstjoin") != false) {
    	player.setExp(plugin.getConfig().getInt("firstjoin.xp"));
    	player.setLevel(plugin.getConfig().getInt("firstjoin.lvl"));
     }
    }
    }
    
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){
    	String message = event.getMessage();
    	if (plugin.getConfig().getBoolean("chat.chattag") != false) {
    		message = event.getMessage().replace("%level", Integer.toString(event.getPlayer().getLevel()));
            event.setMessage(message);
    	}
    }
    
	private String levelFormating(Player p) {
		int l = p.getLevel();
		ChatColor color = ChatColor.WHITE;
		String returning = null;
		
		if(l > 1 && l < 3)
			color = ChatColor.GREEN;
		else if (l > 3 && l < 4)
			color = ChatColor.YELLOW;
		else if (l > 5 && l < 6)
			color = ChatColor.AQUA;
		else if (l > 6 && l < 7)
			color = ChatColor.BLUE;
		else if (l > 7 && l < 8)
			color = ChatColor.DARK_GRAY;
		else if(l >= 8)
			returning = ChatColor.BLACK + "[" + ChatColor.GOLD + ChatColor.BOLD + l + ChatColor.BLACK + " " + "LVL" + "]" + ChatColor.RESET;
		
		if(color == ChatColor.WHITE && returning != null)
			return returning;
		else
			return color + "[" + l + " " + "LVL" + "]" + ChatColor.RESET;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatAddLevel(PlayerChatEvent e) {
		Player p = e.getPlayer();
	 if (plugin.getConfig().getBoolean("chat.chattag") != false) {
		if(p.isOp()) {
			p.setDisplayName(levelFormating(p) + " " + p.getName());
		} else {
			p.setDisplayName(levelFormating(p) + " " + p.getName());
		}
	 }
	}
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		
//		player.setMaxHealth(player.getMaxHealth());
//		player.setHealth(player.getMaxHealth());
        scaleHealth(player);
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();
		
//		player.setMaxHealth(player.getMaxHealth());
//		player.setHealth(player.getMaxHealth());
        scaleHealth(player);
    }
	
    // Функция проверки и добавления дополнительных сердец
	@EventHandler (priority = EventPriority.MONITOR)
    private void scaleHealth(Player player) {
    	
        ArrayList<String> hpList = new ArrayList<>(plugin.getConfig().getStringList("hp"));
        if (!hpList.get(0).equals("none")) {
          for (String hp : hpList) {

        	int levelCompare = Integer.parseInt(hp.split(" ")[0] )- 1;
        	int hpAmount = Integer.parseInt(hp.split(" ")[1]);
            int lvl = player.getLevel();
            int maxHP;
            int getHpUp = hpAmount-20;
            
            if (lvl == levelCompare && lvl > 0) {
                 maxHP = hpAmount;
                 player.setMaxHealth(maxHP);
                 
                 // Пишем в чат, когда добавляются дополнительные сердца
                 if (plugin.getConfig().getBoolean("messages.msghpup") != false) {
                 player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.hpup") + getHpUp + "!");
                 }
                 return;
            }
            }
          
            // Пишем в чат, когда получаем максимальное количество сердец         
            if (player.getLevel() > 0) {
            	
            	if (plugin.getConfig().getBoolean("messages.msgmaxhp") != false) {
      	        player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.maxhp"));
            	}
            	}
        }
    }
}