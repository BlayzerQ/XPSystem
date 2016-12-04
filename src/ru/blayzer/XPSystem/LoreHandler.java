package ru.blayzer.XPSystem;

import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class LoreHandler implements Listener {
	
   private static Plugin plugin;

   public LoreHandler(Plugin pluginIn) {
       plugin = pluginIn;
   }
   
   // Получение игрока
   private Player GetPlayer(String player_name) {
      return this.plugin.getServer().getPlayer(player_name);
   }

   // Проверка при закритии инвентаря
   @EventHandler
   void onInventoryClose(InventoryCloseEvent event) {
      Player player = this.GetPlayer(event.getPlayer().getName());
      if (plugin.getConfig().getBoolean("items.itemreqlvl") != false) {
      this.ValidateEquipedItems(player);
      player.updateInventory();
      }
   }

   // Проверка взаимодействия игрока с предметом
   @EventHandler
   void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      ItemStack item = event.getItem();
      if (plugin.getConfig().getBoolean("items.itemreqlvl") != false) {
      if(!this.MeetsRequirements(player, item)) {
    	  event.setCancelled(true);
      }

      this.ValidateEquipedItems(player);
      }
   }

   // Проверка экиперованных вещей
   private void ValidateEquipedItems(Player player) {
	    if(player != null) {
	        ItemStack item = player.getInventory().getItemInHand();
	        if(item != null && item.getType() != Material.AIR && !this.MeetsRequirements(player, item)) {
	            this.DropEquipedItem(player, item);
	        }
	       
	        if (player.getEquipment().getArmorContents() == null) {
	            return;
	        }
	       
	        ItemStack[] var5 = player.getEquipment().getArmorContents();
	       
	        for(ItemStack is : var5) {
	            if(is != null && is.getType() != Material.AIR && !this.MeetsRequirements(player, is)) {
	                this.DropEquipedItem(player, is);
	            }
	        }
	    }
	}

   // Выбрасывать предметы
   private void DropEquipedItem(Player player, ItemStack item) {
      if(item != null) {
         PlayerInventory inventory = player.getInventory();
         World world = player.getWorld();
         if(item.equals(inventory.getBoots())) {
            inventory.setBoots((ItemStack)null);
            world.dropItemNaturally(player.getLocation(), item);
         } else if(item.equals(inventory.getLeggings())) {
            inventory.setLeggings((ItemStack)null);
            world.dropItemNaturally(player.getLocation(), item);
         } else if(item.equals(inventory.getChestplate())) {
            inventory.setChestplate((ItemStack)null);
            world.dropItemNaturally(player.getLocation(), item);
         } else if(item.equals(inventory.getHelmet())) {
            inventory.setHelmet((ItemStack)null);
            world.dropItemNaturally(player.getLocation(), item);
         } else if(item.equals(inventory.getItemInHand())) {
            inventory.setItemInHand((ItemStack)null);
            world.dropItemNaturally(player.getLocation(), item);
         }
         if (plugin.getConfig().getBoolean("messages.msgitemreqlvl") != false) {
         player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.prefix") + ChatColor.WHITE + plugin.getConfig().getString("messages.itemreqlvl"));
         }
      }
   }

   // Метод, для вызова в проверках уровня игрока и лора
   private boolean MeetsRequirements(Player player, ItemStack item) {
      return player != null && item != null?this.HasRequiredLevel(player, item):true;
   }

   // Проверка содержания лора и проверка уровня лоря с уровнем игрока
   private boolean HasRequiredLevel(Player player, ItemStack item) {
      int player_level = player.getLevel();
      List lore = null;
      if(item.getItemMeta().hasLore()) {
         lore = item.getItemMeta().getLore();
      }

      int required_level = 0;
      if(lore != null) {
         Iterator var7 = lore.iterator();

         while(var7.hasNext()) {
            String lore_entry = (String)var7.next();
            if(lore_entry.contains(plugin.getConfig().getString("items.lore"))) {
               required_level = this.GetInteger(lore_entry);
            }
         }
      }

      return player_level >= required_level;
   }

   // Проверка уровня для лора
   private int GetInteger(String entry) {
      String str_integer = entry.subSequence(entry.indexOf("[") + 1, entry.indexOf("]")).toString();
      int integer = -1;

      try {
         integer = Integer.parseInt(str_integer);
      } catch (Exception var5) {
         ;
      }

      return integer;
   }
}
