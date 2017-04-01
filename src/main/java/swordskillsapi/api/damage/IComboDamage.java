package swordskillsapi.api.damage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;

/**
 * 
 * Controls whether a {@link DamageSource} should have its damage applied to a Combo.
 *
 */
public interface IComboDamage {

	/**
	 * Return true if this damage source counts as Combo damage
	 */
	boolean isComboDamage(EntityPlayer player);

	/**
	 * 
	 * Provide further control over how this {@link DamageSource} interacts with Combos.
	 * Note that none of these methods will be called if {@link #isComboDamage} returns false.
	 *
	 */
	public static interface IComboDamageFull extends IComboDamage {

		/**
		 * Return true to increase the current Combo's hit count when this damage is applied
		 */
		boolean increaseComboCount(EntityPlayer player);

		/**
		 * Only called if {@link #increaseComboCount} returns false.
		 * @return True to add this damage to the previous combo hit, if any
		 */
		boolean applyDamageToPrevious(EntityPlayer player);

		/**
		 * Return true to play the default combo hit sound or false to use {@link #getHitSound}
		 */
		boolean playDefaultSound(EntityPlayer player);

		/**
		 * Return the sound to play on a successful hit, or null to not play any sound.
		 * Only used if {@link #playDefaultSound} returns false.
		 */
		SoundEvent getHitSound(EntityPlayer player);

	}
}
