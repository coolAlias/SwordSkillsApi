package swordskillsapi.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * 
 * Items implementing this interface determine for themselves how they interact
 * with skills whose use requires a weapon or sword to be held.
 * 
 * If a skill's activation requires blocking, the item must be able to block or
 * it will not be able to activate such skills.
 * 
 * Some skills may only be performed while wielding a {@link #isSword sword}; these are:
 * {@code LeapingBlow}, {@code MortalDraw}, {@code RisingCut}, and {@code SwordBeam}.
 * 
 * For items that do not use NBT or stack damage, consider registering them as weapons
 * or as swords via the {@link WeaponRegistry} using FML's Inter-Mod Communications.
 *
 */
public interface IWeapon {

	/**
	 * Return true if the ItemStack is considered a sword.
	 * Consider returning !{@link WeaponRegistry#isSwordForbidden(Item)} to allow users to choose the item's sword status.
	 */
	boolean isSword(ItemStack stack);

	/**
	 * Return true if the ItemStack is considered a weapon; if {@link #isSword} returns true, this should also return true.
	 * Consider returning !{@link WeaponRegistry#isWeaponForbidden(Item)} to allow users to choose the item's weapon status.
	 */
	boolean isWeapon(ItemStack stack);

}
