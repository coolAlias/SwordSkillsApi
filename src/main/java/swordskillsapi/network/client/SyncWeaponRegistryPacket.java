package swordskillsapi.network.client;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import swordskillsapi.SwordSkillsApi;
import swordskillsapi.api.item.WeaponRegistry;
import swordskillsapi.network.AbstractMessage.AbstractClientMessage;

/**
 *
 * Sends a single WeaponRegistry entry to a client
 *
 */
public class SyncWeaponRegistryPacket extends AbstractClientMessage<SyncWeaponRegistryPacket>
{
	private String registry_name;

	private ResourceLocation location;

	public SyncWeaponRegistryPacket() {}

	public SyncWeaponRegistryPacket(WeaponRegistry.WeaponRegistryHolder registry, Item item) {
		this.registry_name = registry.name;
		this.location = item.getRegistryName();
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.registry_name = buffer.readString(24);
		String s = buffer.readString(256);
		this.location = WeaponRegistry.getResourceLocation(s);
		if (this.location == null) {
			SwordSkillsApi.LOGGER.error("Invalid resource location string received in SyncWeaponRegistryPacket: " + s);
		}
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(this.registry_name);
		buffer.writeString(this.location.toString());
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (this.location != null) {
			WeaponRegistry.INSTANCE.syncWeaponRegistryEntry(this.registry_name, this.location);
		}
	}
}
