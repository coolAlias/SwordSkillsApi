package swordskillsapi.command;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import swordskillsapi.SwordSkillsApi;
import swordskillsapi.api.item.WeaponRegistry;

public class CommandWeaponRegistry extends CommandBase
{
	public static final ICommand INSTANCE = new CommandWeaponRegistry();

	private CommandWeaponRegistry() {}

	@Override
	public String getCommandName() {
		return SwordSkillsApi.ID;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * swordskillsapi <allow|forbid|is> <sword|weapon> modid:item_name
	 */
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.swordskillsapi.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args == null || args.length != 3) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		ResourceLocation location = WeaponRegistry.getResourceLocation(args[2]);
		if (location == null) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		Item item = Item.REGISTRY.getObject(location);
		if (item == null) {
			throw new WrongUsageException("commands.swordskillsapi.item.unknown", args[2]);
		}
		boolean isSword = isSword(args[1]);
		String msg = "commands.swordskillsapi.";
		if (args[0].equalsIgnoreCase("is")) {
			boolean is = (isSword ? WeaponRegistry.INSTANCE.isSword(item) : WeaponRegistry.INSTANCE.isWeapon(item));
			msg += "is." + (is ? "true" : "false");
		} else if (args[0].equalsIgnoreCase("allow")) {
			msg += "allow.";
			if (isSword) {
				msg += (WeaponRegistry.INSTANCE.registerSword("Command", item, true) ? "success" : "fail");
			} else if (WeaponRegistry.INSTANCE.registerWeapon("Command", item, true)) {
				msg += "success";
			} else {
				msg += "fail";
			}
		} else if (args[0].equalsIgnoreCase("forbid")) {
			msg += "forbid.";
			if (isSword) {
				msg += (WeaponRegistry.INSTANCE.removeSword("Command", item, true) ? "success" : "fail");
			} else if (WeaponRegistry.INSTANCE.removeWeapon("Command", item, true)) {
				msg += "success";
			} else {
				msg += "fail";
			}
		} else {
			throw new WrongUsageException("commands.swordskillsapi.action.unknown");
		}
		String type = "commands.swordskillsapi." + (isSword ? "sword" : "weapon");
		sender.addChatMessage(new TextComponentTranslation(msg, args[2], new TextComponentTranslation(type)));
	}

	private boolean isSword(String arg) throws CommandException {
		if (arg.equalsIgnoreCase("sword")) {
			return true;
		} else if (arg.equalsIgnoreCase("weapon")) {
			return false;
		}
		throw new WrongUsageException("commands.swordskillsapi.type.unknown");
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		switch (args.length) {
		case 1: return CommandBase.getListOfStringsMatchingLastWord(args, "allow", "forbid", "is");
		case 2: return CommandBase.getListOfStringsMatchingLastWord(args, "sword", "weapon");
		}
		return null;
	}
}
