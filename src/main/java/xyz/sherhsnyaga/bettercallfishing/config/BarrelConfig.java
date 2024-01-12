package xyz.sherhsnyaga.bettercallfishing.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.sherhsnyaga.bettercallfishing.utils.IA_Hook;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;

import java.util.*;

public class BarrelConfig {
    private final Random random;
    @Getter
    private boolean isEnable;
    private int catchChance;
    private final List<ItemSettings> itemSettingsList;

    public BarrelConfig(FileConfiguration configuration) {
        random = new Random();
        itemSettingsList = new ArrayList<>();
        setConfiguration(configuration);
    }

    public void setConfiguration(FileConfiguration config) {
        itemSettingsList.clear();
        isEnable = config.getBoolean("enable-barrel-catch");
        catchChance = config.getInt("barrel-catch-chance");

        Set<String> keys = config.getConfigurationSection("barrel-items").getKeys(false);
        boolean iaEnable = IA_Hook.isEnable();
        for (String key: keys) {
            int chance = config.getInt("barrel-items." + key + ".chance");
            int minCount = config.getInt("barrel-items." + key + ".min-count");
            int maxCount = config.getInt("barrel-items." + key + ".max-count");
            ItemSettings settings = null;
            if(iaEnable && key.startsWith("IA:") && CustomStack.isInRegistry(key.replaceAll("IA:", ""))){
                settings = new ItemSettings(CustomStack.getInstance(key.replaceAll("IA:", "")).getItemStack(), chance, minCount, maxCount, 0);
            }else if(!key.startsWith("IA:")){
                settings = new ItemSettings(new ItemStack(Material.getMaterial(key)), chance, minCount, maxCount, 0);
            }
            if(settings!=null) itemSettingsList.add(settings);
        }
    }

    public HashMap<Integer, ItemStack> generateBarrelInventoryMap() {
        HashMap<Integer, ItemStack> inventory = new HashMap<>();

        List<Integer> slotList = new ArrayList<>();

        for (int i=0; i<27; i++)
            slotList.add(i);

        List<ItemSettings> itemSettings = new ArrayList<>();

        for (ItemSettings i: itemSettingsList)
            itemSettings.add(new ItemSettings(i.item, i.chance, i.minCount, i.maxCount, 0));

        while (!slotList.isEmpty()) {
            int index = random.nextInt(slotList.size());
            int slot = slotList.get(index);

            for (ItemSettings itemData: itemSettings) {
                if (itemData.counter >= itemData.maxCount)
                    continue;
                ItemStack item = itemData.item;
                if (itemData.counter == 0) {
                    if (getRandom(0, 100) < itemData.chance) {
                        int itemCount = getRandom(itemData.minCount, itemData.maxCount);
                        item.setAmount(itemCount);
                        inventory.put(slot,item);
                        itemData.counter = itemData.counter + itemCount;
                    }
                }
                else {
                    if (getRandom(0, 100) < itemData.chance) {
                        int itemCount = getRandom(1, itemData.maxCount - itemData.counter);
                        item.setAmount(itemCount);
                        inventory.put(slot,item);
                        itemData.counter = itemData.counter + itemCount;
                    }
                }
            }

            slotList.remove(index);
        }

        return inventory;
    }

    public boolean testBarrelCatch() {
        if (!isEnable)
            return false;

        return random.nextInt(100) <= catchChance;
    }

    private int getRandom(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    @AllArgsConstructor
    @Getter
    private class ItemSettings {
        private ItemStack item;
        private int chance;
        private int minCount;
        private int maxCount;
        @Setter
        private int counter;
    }
}
