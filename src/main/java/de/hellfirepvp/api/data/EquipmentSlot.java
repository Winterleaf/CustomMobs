package de.hellfirepvp.api.data;

import de.hellfirepvp.lang.LanguageHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * This class is part of the CustomMobs Plugin
 * The plugin can be found at: https://www.spigotmc.org/resources/custommobs.7339
 * Class: EquipmentSlot
 * Created by HellFirePvP
 * Date: 27.05.2016 / 01:23
 */
public enum EquipmentSlot {

    MAIN_HAND("main"),
    OFF_HAND("off"),
    HELMET("helmet"),
    CHESTPLATE("chest"),
    LEGGINGS("leggings"),
    BOOTS("boots");

    private final String subKey;

    private EquipmentSlot(String subKey) {
        this.subKey = subKey;
    }

    public String getUnlocalizedRepresentation() {
        return "equipment." + subKey;
    }

    public String getLocalizedName() {
        return LanguageHandler.translate(getUnlocalizedRepresentation());
    }

    public ItemStack getStackFromPlayer(Player player) {
        PlayerInventory inv = player.getInventory();
        switch (this) {
            case MAIN_HAND:
                return inv.getItemInMainHand();
            case OFF_HAND:
                return inv.getItemInOffHand();
            case HELMET:
                return inv.getHelmet();
            case CHESTPLATE:
                return inv.getChestplate();
            case LEGGINGS:
                return inv.getLeggings();
            case BOOTS:
                return inv.getBoots();
        }
        return null;
    }

}