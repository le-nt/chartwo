package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arathain.charter.Charter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


import java.util.UUID;

public class CharterComponent implements SendHelpComponent {
	private BlockPos charterStonePos;
	private UUID owner;
	private World world;

	public CharterComponent(World newWorld) {
		world = newWorld;
	}

	public CharterComponent(BlockPos charterStone, PlayerEntity owner, World world) {
		this.charterStonePos = charterStone;
		this.owner = owner.getUuid();
		this.area.add(Box.of(Vec3d.of(charterStone), 15, 15, 15));
		this.members.add(this.owner);
	}

	public void tick() {

	}

	public BlockPos getCharterStonePos() {
		return charterStonePos;
	}

	public UUID getCharterOwnerUuid() {
		return owner;
	}

	public void writeToNbt(NbtCompound tag) {
		NbtCompound rootTag = new NbtCompound();
		NbtList areaListTag = new NbtList();
		NbtList memberListTag = new NbtList();

		tag.put(Charter.MODID, rootTag);
		rootTag.putUuid("CharterOwner", owner);
		rootTag.put("CharterStonePos", NbtHelper.fromBlockPos(charterStonePos));

		for(Box box : area) {
			NbtCompound boxCompound = new NbtCompound();

			boxCompound.put("Center", NbtHelper.fromBlockPos(new BlockPos(box.getCenter().x, box.getCenter().y, box.getCenter().z)));
			boxCompound.putDouble("LengthX", box.getXLength());
			boxCompound.putDouble("LengthY", box.getXLength());
			boxCompound.putDouble("LengthZ", box.getXLength());
			areaListTag.add(boxCompound);
		}

		for(UUID member : members)
			memberListTag.add(NbtHelper.fromUuid(member));

		rootTag.put("CharterArea", areaListTag);
		rootTag.put("CharterMembers", memberListTag);
	}

	public void readFromNbt(NbtCompound tag) {
		NbtCompound rootTag = tag.getCompound(Charter.MODID);
		NbtList areaListTag = tag.getList("CharterArea", NbtElement.COMPOUND_TYPE);
		NbtList memberListTag = tag.getList("CharterMembers", NbtElement.INT_ARRAY_TYPE);
		area.clear();
		members.clear();

		owner = rootTag.getUuid("CharterOwner");
		charterStonePos = NbtHelper.toBlockPos(rootTag.getCompound("CharterStonePos"));

		for(NbtElement boxElement : areaListTag) {
			NbtCompound boxCompound = (NbtCompound) boxElement;
			area.add(Box.of(Vec3d.of(NbtHelper.toBlockPos(boxCompound.getCompound("Center"))), boxCompound.getDouble("LengthX"), boxCompound.getDouble("LengthY"), boxCompound.getDouble("LengthZ")));
		}

		for(NbtElement member : memberListTag) {
			members.add(NbtHelper.toUuid(member));
		}
	}
}
