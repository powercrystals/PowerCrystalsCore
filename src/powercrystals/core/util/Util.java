package powercrystals.core.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercrystals.core.position.BlockPosition;

public class Util
{
	public static boolean isBlockUnbreakable(World world, int x, int y, int z)
	{
		Block b = Block.blocksList[world.getBlockId(x, y, z)];
		return b instanceof BlockFluid || b.getBlockHardness(world, x, y, z) < 0;
	}
	
	public static boolean isRedstonePowered(World world, int x, int y, int z)
	{
		if(world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			return true;
		}
		for(BlockPosition bp : new BlockPosition(x, y, z).getAdjacent(false))
		{
			int blockId = world.getBlockId(bp.x, bp.y, bp.z);
			if(blockId == Block.redstoneWire.blockID && Block.blocksList[blockId].isProvidingStrongPower(world, bp.x, bp.y, bp.z, 1))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isRedstonePowered(TileEntity te)
	{
		return isRedstonePowered(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}
}
