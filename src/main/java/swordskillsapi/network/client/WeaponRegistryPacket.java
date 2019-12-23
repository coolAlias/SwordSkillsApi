package swordskillsapi.network.client;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import swordskillsapi.api.item.WeaponRegistry;
import swordskillsapi.network.AbstractMessage.AbstractClientMessage;

/**
 *
 * Sends the entire WeaponRegistry to a client; recommended to use only when a client first logs in
 *
 */
public class WeaponRegistryPacket extends AbstractClientMessage<WeaponRegistryPacket>
{
	private WeaponRegistry registry;

	public WeaponRegistryPacket() {}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		registry = new WeaponRegistry();
		registry.readFromBuffer(buffer);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		WeaponRegistry.INSTANCE.writeToBuffer(buffer);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		WeaponRegistry.INSTANCE.copy(registry);
	}
}
