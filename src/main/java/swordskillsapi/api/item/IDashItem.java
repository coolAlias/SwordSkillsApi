package swordskillsapi.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * 
 * Allows item held in main hand to determine whether it is
 * suitable for using the Dash skill, regardless of whether
 * it can be used to block or not.
 *
 */
public interface IDashItem
{

	/**
	 * Return whether the player is allowed to use this item to Dash 
	 * @param stack  The item held
	 * @param player The player attempting to Dash
	 * @param hand   The hand in which the item is held
	 * @return True to allow the player to Dash, provided all other requirements are met
	 */
	boolean canDash(ItemStack stack, EntityPlayer player, EnumHand hand);

}
