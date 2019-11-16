package swordskillsapi.api.item;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import swordskillsapi.SwordSkillsApi;

/**
 * 
 * Allows items from other mods to be registered as generic weapons or as swords.
 * Items that rely on NBT or damage value to determine their weapon status should
 * NOT be registered in this way - use {@link IWeapon} instead.
 * 
 * Note also that some skills require blocking to activate, so any item which cannot
 * be used to block will not be able to trigger those skills even if registered.
 *
 */
public class WeaponRegistry
{
	/** Use {@link IMC_ALLOW_SWORD} instead */
	@Deprecated
	public static final String IMC_SWORD_KEY = "ZssRegisterSword";

	/** Use {@link IMC_ALLOW_WEAPON} instead */
	@Deprecated
	public static final String IMC_WEAPON_KEY = "ZssRegisterWeapon";

	/** FML Inter-Mod Communication key for adding an item to the list of allowed swords */
	public static final String IMC_ALLOW_SWORD = "allow_sword";

	/** FML Inter-Mod Communication key for adding an item to the list of allowed non-sword melee weapons */
	public static final String IMC_ALLOW_WEAPON = "allow_weapon";

	/** FML Inter-Mod Communication key for adding an item to the list of items disallowed as swords */
	public static final String IMC_FORBID_SWORD = "forbid_sword";

	/** FML Inter-Mod Communication key for adding an item to the list of items disallowed as non-sword melee weapons */
	public static final String IMC_FORBID_WEAPON = "forbid_weapon";

	/** FML Inter-Mod Communication suffix for adding or removing an item in override mode */
	public static final String IMC_OVERRIDE = "_override";

	private final WeaponRegistryHolder allowed_swords = new WeaponRegistryHolder("Allowed Swords");

	private final WeaponRegistryHolder allowed_weapons = new WeaponRegistryHolder("Allowed Weapons");

	private final WeaponRegistryHolder forbidden_swords = new WeaponRegistryHolder("Forbidden Swords");

	private final WeaponRegistryHolder forbidden_weapons = new WeaponRegistryHolder("Forbidden Weapons");

	public static final WeaponRegistry INSTANCE = new WeaponRegistry();

	private WeaponRegistry() {}

	/**
	 * Returns true if the item is considered a sword and has not been forbidden as such
	 */
	public boolean isSword(Item item) {
		return !isSwordForbidden(item) && (item instanceof ItemSword || allowed_swords.contains(item));
	}

	/**
	 * Returns true if the item is forbidden either as a sword or a weapon (if it's not a weapon, it's not a sword).
	 * Recommended to use this method instead of {@link #isSword} when implementing {@link IWeapon#isSword}.
	 */
	public boolean isSwordForbidden(Item item) {
		return forbidden_swords.contains(item) || isWeaponForbidden(item);
	}

	/**
	 * Returns true if the item is considered a melee weapon of any kind and has not been forbidden as such
	 */
	public boolean isWeapon(Item item) {
		return !isWeaponForbidden(item) && (item instanceof ItemSword || item instanceof ItemAxe || allowed_weapons.contains(item));
	}

	/**
	 * Returns true if the item is forbidden as a weapon.
	 * Recommended to use this method instead of {@link #isWeapon} when implementing {@link IWeapon#isWeapon}.
	 */
	public boolean isWeaponForbidden(Item item) {
		return forbidden_weapons.contains(item);
	}

	/**
	 * If the message key is either {@link #IMC_SWORD_KEY} or {@link #IMC_WEAPON_KEY}
	 * and the message contains an ItemStack, the stack will be registered appropriately.
	 */
	public void processMessage(FMLInterModComms.IMCMessage msg) {
		String origin = "IMC:" + msg.getSender();
		String method = msg.key.toLowerCase();
		boolean override = method.endsWith(IMC_OVERRIDE);
		if (override) {
			method = method.substring(0, method.length() - IMC_OVERRIDE.length());
		}
		if (!msg.isItemStackMessage()) {
			return;
		} else if (method.equals(IMC_ALLOW_SWORD) || method.equals(IMC_SWORD_KEY)) {
			registerSword(origin, msg.getItemStackValue().getItem(), override);
		} else if (method.equals(IMC_ALLOW_WEAPON) || method.equals(IMC_WEAPON_KEY)) {
			registerWeapon(origin, msg.getItemStackValue().getItem(), override);
		} else if (method.equals(IMC_FORBID_SWORD)) {
			removeSword(origin, msg.getItemStackValue().getItem(), override);
		} else if (method.equals(IMC_FORBID_WEAPON)) {
			removeWeapon(origin, msg.getItemStackValue().getItem(), override);
		} else {
			SwordSkillsApi.LOGGER.warn(String.format("[WeaponRegistry] Invalid IMC message method name %s from mod %s", msg.key, msg.getSender()));
		}
	}

	/**
	 * Registers an array of named items either as swords or generic weapons
	 * @param names Must be in the format 'modid:registered_item_name'
	 * @param origin Information about the origin of the names array, e.g. "{mod_id}" or "Config:{mod_id}"
	 * @param isSword True to register the items as swords, or false for generic melee weapons
	 */
	public void registerItems(String[] names, String origin, boolean isSword) {
		processArray(names, origin, isSword, true);
	}

	/**
	 * Forbids an array of named items either from the swords or generic weapons registry
	 * @param names Must be in the format 'modid:registered_item_name'
	 * @param origin Information about the origin of the names array, e.g. "{mod_id}" or "Config:{mod_id}"
	 * @param isSword True to forbid the items as swords, or false for generic melee weapons
	 */
	public void forbidItems(String[] names, String origin, boolean isSword) {
		processArray(names, origin, isSword, false);
	}

	private void processArray(String[] names, String origin, boolean isSword, boolean register) {
		for (String s : names) {
			String[] parts = parseString(s);
			if (parts != null) {
				Item item = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
				if (item == null) {
					SwordSkillsApi.LOGGER.warn(String.format("[WeaponRegistry] [%s] %s could not be found - the mod may not be installed or it may have been typed incorrectly", origin, s));
				} else if (isSword) {
					if (register) {
						registerSword(origin, parts[0], item);
					} else {
						removeSword(origin, parts[0], item);
					}
				} else if (register) {
					registerWeapon(origin, parts[0], item);
				} else {
					removeWeapon(origin, parts[0], item);
				}
			}
		}
	}

	/**
	 * Calls {@link #registerSword(String, Item, boolean)} in non-strict mode
	 */
	public boolean registerSword(String origin, String modid, Item item) {
		return registerSword(origin+":"+modid, item, false);
	}

	/**
	 * Registers an item as a sword: it may be used to activate sword-specific skills, cut grass, etc.
	 * See {@link #registerItem(WeaponRegistryHolder, String, Item, boolean)} for more information.
	 */
	public boolean registerSword(String origin, Item item, boolean override) {
		return registerItem(allowed_swords, origin, item, override);
	}

	/**
	 * Calls {@link #registerWeapon(String, Item, boolean)} in non-strict mode
	 */
	public boolean registerWeapon(String origin, String modid, Item item) {
		return registerWeapon(origin+":"+modid, item, false);
	}

	/**
	 * Registers an item as a generic melee weapon: it may be used to activate all skills that do not specifically require a sword.
	 * See {@link #registerItem(WeaponRegistryHolder, String, Item, boolean)} for more information.
	 */
	public boolean registerWeapon(String origin, Item item, boolean override) {
		return registerItem(allowed_weapons, origin, item, override);
	}

	/**
	 * Calls {@link #removeSword(String, String, Item, boolean)} in non-strict mode
	 */
	public boolean removeSword(String origin, String modid, Item item) {
		return removeSword(origin+":"+modid, item, false);
	}

	/**
	 * Registers an item as a non-sword, i.e. it may not be used to activate any skills requiring a sword, but it may still be considered a weapon
	 * See {@link #registerItem(WeaponRegistryHolder, String, Item, boolean)} for more information.
	 */
	public boolean removeSword(String origin, Item item, boolean override) {
		return registerItem(forbidden_swords, origin, item, override);
	}

	/**
	 * Calls {@link #removeWeapon(String, Item, boolean)} in non-strict mode
	 */
	public boolean removeWeapon(String origin, String modid, Item item) {
		return removeWeapon(origin+":"+modid, item, false);
	}

	/**
	 * Registers an item as a non-weapon, i.e. it may not be used to activate any skills
	 * See {@link #registerItem(WeaponRegistryHolder, String, Item, boolean)} for more information.
	 */
	public boolean removeWeapon(String origin, Item item, boolean override) {
		return registerItem(forbidden_weapons, origin, item, override);
	}

	/**
	 * Adds an item to the specified registry and removes it from all others
	 * @param registry See class fields for available registries
	 * @param origin String containing information about origins of registration, e.g. "IMC:{mod_id}"
	 * @param item Item to add to the specified registry
	 * @param override Boolean, whether to perform the operation even if the Item is already registered to another list
	 * @return true if item was successfully added
	 */
	private boolean registerItem(final WeaponRegistryHolder registry, String origin, Item item, boolean override) {
		Optional<WeaponRegistryHolder> match = getRegisteredList(item, registry);
		if (match.isPresent()) {
			if (!override) {
				SwordSkillsApi.LOGGER.error(String.format("[WeaponRegistry] [%s] failed to add %s to the %s list - already on the %s list", origin, item.getRegistryName().toString(), registry.name, match.get().name));
				return false;
			}
			unRegister(origin, item, registry);
		}
		if (registry.items.add(item)) {
			SwordSkillsApi.LOGGER.info(String.format("[WeaponRegistry] [%s] Added %s to the %s list", origin, item.getRegistryName().toString(), registry.name));
			return true;
		}
		SwordSkillsApi.LOGGER.info(String.format("[WeaponRegistry] [%s] %s was already on the %s list", origin, item.getRegistryName().toString(), registry.name));
		return false;
	}

	/**
	 * Returns the first registry to which the Item has been registered, if any
	 * @param registry Registry to exclude from the possible results, if any
	 */
	private Optional<WeaponRegistryHolder> getRegisteredList(final Item item, @Nullable final WeaponRegistryHolder registry) {
		return Stream.of(allowed_swords, allowed_weapons, forbidden_swords, forbidden_weapons)
				.filter(s -> s != registry && s.contains(item))
				.findFirst();
	}

	/**
	 * Removes the item from all registries except for the one specified
	 */
	private void unRegister(final String origin, final Item item, @Nullable final WeaponRegistryHolder registry) {
		Stream.of(allowed_swords, allowed_weapons, forbidden_swords, forbidden_weapons)
		.forEach(s -> {
			if (s != registry && s.items.remove(item)) {
				SwordSkillsApi.LOGGER.info(String.format("[WeaponRegistry] [%s] Removed %s from list of %s", origin, item.getRegistryName().toString(), s.name));
			}
		});
	}

	/**
	 * Parses a String into an array containing the mod_id and item_name, or NULL if format was invalid
	 * @param itemid Expected format is 'modid:registered_item_name'
	 */
	public static String[] parseString(String itemid) {
		String[] parts = itemid.split(":");
		if (parts.length == 2) {
			return parts;
		}
		SwordSkillsApi.LOGGER.error(String.format("[WeaponRegistry] String must be in the format 'modid:registered_item_name', received: %s", itemid));
		return null;
	}

	private static class WeaponRegistryHolder
	{
		private final String name;

		private final Set<Item> items = new HashSet<Item>();

		public WeaponRegistryHolder(String name) {
			this.name = name;
		}

		public boolean contains(Item item) {
			return this.items.contains(item);
		}
	}
}
