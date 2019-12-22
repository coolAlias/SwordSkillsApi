package swordskillsapi.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import swordskillsapi.network.PacketDispatcher;
import swordskillsapi.network.client.WeaponRegistryPacket;

public class ModEventHandler
{
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			PacketDispatcher.sendTo(new WeaponRegistryPacket(), (EntityPlayerMP) event.player);
		}
	}
}
