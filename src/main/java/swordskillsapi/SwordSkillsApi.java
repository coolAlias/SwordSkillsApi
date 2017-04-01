package swordskillsapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = SwordSkillsApi.ID, name = SwordSkillsApi.NAME, version = SwordSkillsApi.VERSION, updateJSON = SwordSkillsApi.VERSION_LIST)
public class SwordSkillsApi
{
	public static final String ID = "swordskillsapi";
	public static final String NAME = "Sword Skills API";
	public static final String VERSION = "1.0";
	public static final String VERSION_LIST = "https://raw.githubusercontent.com/coolAlias/SwordSkillsApi/master/src/main/resources/versionlist.json";

	@Mod.Instance(ID)
	public static SwordSkillsApi instance;

	@SidedProxy(clientSide = ID + ".ClientProxy", serverSide = ID + ".CommonProxy")
	public static CommonProxy proxy;

	public static final Logger LOGGER = LogManager.getLogger(ID);

}
