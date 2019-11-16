package swordskillsapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import swordskillsapi.api.item.WeaponRegistry;
import swordskillsapi.command.CommandWeaponRegistry;

@Mod(modid = SwordSkillsApi.ID, name = SwordSkillsApi.NAME, version = SwordSkillsApi.VERSION, updateJSON = SwordSkillsApi.VERSION_LIST)
public class SwordSkillsApi
{
	public static final String ID = "swordskillsapi";
	public static final String NAME = "Sword Skills API";
	public static final String VERSION = "1.1.0";
	public static final String VERSION_LIST = "https://raw.githubusercontent.com/coolAlias/SwordSkillsApi/master/src/main/resources/versionlist.json";

	@Mod.Instance(ID)
	public static SwordSkillsApi instance;

	@SidedProxy(clientSide = ID + ".ClientProxy", serverSide = ID + ".CommonProxy")
	public static CommonProxy proxy;

	public static final Logger LOGGER = LogManager.getLogger(ID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.preInit(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Config.postInit();
	}

	@Mod.EventHandler
	public void processMessages(FMLInterModComms.IMCEvent event) {
		for (final FMLInterModComms.IMCMessage msg : event.getMessages()) {
			WeaponRegistry.INSTANCE.processMessage(msg);
		}
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(CommandWeaponRegistry.INSTANCE);
	}
}
