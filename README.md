Sword Skills API
================
This API is designed for third party mod developers to add compatibility with both the Dynamic Sword Skills and Zelda Sword Skills mods.

As such, it is required to be installed in order to run either DSS or ZSS.

Players
-------
Certain skills in DSS and ZSS require a sword to activate, while others can be activated by any melee weapon. By default, ItemSword items are considered swords, and ItemAxe items are considered melee weapons.

Use this mod's config settings to customize which items are considered to be swords and weapons. An item may only be on one list at a time.

Use the "forbidden" lists to remove an item that is otherwise considered a sword or weapon. For example, "minecraft:wooden_sword" is considered a sword by default; if added to the forbidden swords list, wooden swords will no longer be able to activate any skills requiring a sword.

Use the `/swordskillsapi` command to modify the WeaponRegistry in-game; changes made via command persist only for the current server session.

Mod Developers
--------------
Use this API if you have an Item that you wish to control how it interacts with sword skill use or a custom DamageSource that you wish to control how it interacts with sword combos, for example.

To do so, include the deobfuscated .jar in your /eclipse/libs or a similar folder and add it to your build path, then implement any necessary interface(s).

If you simply wish to allow or forbid an item as a sword or weapon, you can do so by sending an IMC message using one of the following method names rather than including any API files in your project:

* "allow_sword"

* "allow_weapon"

* "forbid_sword"

* "forbid_weapon"

An item may only be on one of these lists at a time.

If an item may have already been registered by another source and you wish to override it, append "_override" to any of the above method names.
