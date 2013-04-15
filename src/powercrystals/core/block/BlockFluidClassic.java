package powercrystals.core.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFluidClassic extends BlockFluidRoot
{
	protected boolean[] isOptimalFlowDirection = new boolean[4];
	protected int[] flowCost = new int[4];

	public BlockFluidClassic(int id, Material material)
	{
		super(id, material);
	}

	/**
	 * Returns true if the block at (x, y, z) is displaceable. Does not displace
	 * the block.
	 */
	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z)
	{
		int bId = world.getBlockId(x, y, z);
		if(bId == 0)
		{
			return true;
		}
		if(bId == blockID)
		{
			return false;
		}
		if(displacementIds.containsKey(bId))
		{
			return displacementIds.get(bId);
		}
		Material material = Block.blocksList[bId].blockMaterial;
		if(material.blocksMovement() || material == Material.water || material == Material.lava || material == Material.portal)
		{
			return false;
		}
		return true;
	}

	/**
	 * Attempt to displace the block at (x, y, z), return true if it was
	 * displaced.
	 */
	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z)
	{
		int bId = world.getBlockId(x, y, z);
		if(bId == 0)
		{
			return true;
		}
		if(bId == blockID)
		{
			return false;
		}
		if(displacementIds.containsKey(bId))
		{
			if(displacementIds.get(bId))
			{
				Block.blocksList[bId].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				return true;
			}
			return false;
		}
		Material material = Block.blocksList[bId].blockMaterial;
		if(material.blocksMovement() || material == Material.water || material == Material.lava || material == Material.portal)
		{
			return false;
		}
		Block.blocksList[bId].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		return true;
	}

	/* LIQUID FUNCTIONS */
	public boolean isFlowingVertically(IBlockAccess world, int x, int y, int z)
	{
		return world.getBlockId(x, y + densityDir, z) == blockID || world.getBlockId(x, y, z) == blockID && canFlowInto(world, x, y + densityDir, z);
	}

	public boolean isSourceBlock(World world, int x, int y, int z)
	{
		return world.getBlockId(x, y, z) == blockID && world.getBlockMetadata(x, y, z) + 1 == quantaPerBlock;
	}

	protected void updateFlowLevel(World world, int x, int y, int z, int quantaRemaining)
	{

	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living, ItemStack theItem)
	{
		world.setBlock(x, y, z, blockID, quantaPerBlock - 1, 3);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		int quantaRemaining = world.getBlockMetadata(x, y, z) + 1;
		int expQuanta = -101;

		// check adjacent block levels if non-source
		if(quantaRemaining < quantaPerBlock)
		{
			int y2 = y - densityDir;

			if(world.getBlockId(x, y2, z) == blockID || world.getBlockId(x - 1, y2, z) == blockID || world.getBlockId(x + 1, y2, z) == blockID
					|| world.getBlockId(x, y2, z - 1) == blockID || world.getBlockId(x, y2, z + 1) == blockID)
			{
				expQuanta = quantaPerBlock - 1;

			}
			else
			{
				int maxQuanta = -100;
				maxQuanta = getLargerQuanta(world, x - 1, y, z, maxQuanta);
				maxQuanta = getLargerQuanta(world, x + 1, y, z, maxQuanta);
				maxQuanta = getLargerQuanta(world, x, y, z - 1, maxQuanta);
				maxQuanta = getLargerQuanta(world, x, y, z + 1, maxQuanta);

				expQuanta = maxQuanta - 1;
			}
			// decay calculation
			if(expQuanta != quantaRemaining)
			{
				quantaRemaining = expQuanta;
				if(expQuanta <= 0)
				{
					world.setBlockToAir(x, y, z);
				}
				else
				{
					world.setBlockMetadataWithNotify(x, y, z, expQuanta - 1, 3);
					world.scheduleBlockUpdate(x, y, z, blockID, tickRate);
					world.notifyBlocksOfNeighborChange(x, y, z, blockID);
				}
			}
		}
		// Flow vertically if possible
		if(canDisplace(world, x, y + densityDir, z))
		{
			flowIntoBlock(world, x, y + densityDir, z, quantaPerBlock - 2);
			return;
		}
		// Flow outward if possible
		int flowMeta = quantaRemaining - 2;
		if(flowMeta < 0)
		{
			return;
		}

		if(isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
		{

			if(world.getBlockId(x, y - densityDir, z) == blockID)
			{
				flowMeta = quantaPerBlock - 2;
			}

			boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

			if(flowTo[0])
			{
				flowIntoBlock(world, x - 1, y, z, flowMeta);
			}
			if(flowTo[1])
			{
				flowIntoBlock(world, x + 1, y, z, flowMeta);
			}
			if(flowTo[2])
			{
				flowIntoBlock(world, x, y, z - 1, flowMeta);
			}
			if(flowTo[3])
			{
				flowIntoBlock(world, x, y, z + 1, flowMeta);
			}
		}
	}

	protected boolean[] getOptimalFlowDirections(World world, int x, int y, int z)
	{
		for(int side = 0; side < 4; side++)
		{
			flowCost[side] = 1000;

			int x2 = x;
			int y2 = y;
			int z2 = z;

			switch(side)
			{
			case 0:
				--x2;
				break;
			case 1:
				++x2;
				break;
			case 2:
				--z2;
				break;
			case 3:
				++z2;
				break;
			}

			if(!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
			{
				continue;
			}
			if(canFlowInto(world, x2, y2 + densityDir, z2))
			{
				flowCost[side] = 0;
			}
			else
			{
				flowCost[side] = calculateFlowCost(world, x2, y2, z2, 1, side);
			}
		}

		int min = flowCost[0];
		for(int side = 1; side < 4; side++)
		{
			if(flowCost[side] < min)
			{
				min = flowCost[side];
			}
		}
		for(int side = 0; side < 4; side++)
		{
			isOptimalFlowDirection[side] = flowCost[side] == min;
		}
		return isOptimalFlowDirection;
	}

	protected int calculateFlowCost(World world, int x, int y, int z, int recurseDepth, int side)
	{
		int cost = 1000;

		for(int adjSide = 0; adjSide < 4; adjSide++)
		{
			if(adjSide == 0 && side == 1 || adjSide == 1 && side == 0 || adjSide == 2 && side == 3 || adjSide == 3 && side == 2)
			{
				continue;
			}

			int x2 = x;
			int y2 = y;
			int z2 = z;

			switch(adjSide)
			{
			case 0:
				--x2;
				break;
			case 1:
				++x2;
				break;
			case 2:
				--z2;
				break;
			case 3:
				++z2;
				break;
			}

			if(!canFlowInto(world, x2, y2, z2) || isSourceBlock(world, x2, y2, z2))
			{
				continue;
			}
			if(canFlowInto(world, x2, y2 + densityDir, z2))
			{
				return recurseDepth;
			}
			if(recurseDepth >= 4)
			{
				continue;
			}
			int min = calculateFlowCost(world, x2, y2, z2, recurseDepth + 1, adjSide);
			if(min < cost)
			{
				cost = min;
			}
		}
		return cost;
	}

	protected void flowIntoBlock(World world, int x, int y, int z, int meta)
	{
		if(displaceIfPossible(world, x, y, z))
		{
			world.setBlock(x, y, z, this.blockID, meta, 3);
		}
	}

	protected boolean canFlowInto(IBlockAccess world, int x, int y, int z)
	{
		int bId = world.getBlockId(x, y, z);
		if(bId == 0)
		{
			return true;
		}
		if(bId == blockID)
		{
			return true;
		}
		if(displacementIds.containsKey(bId))
		{
			return displacementIds.get(bId);
		}
		Material material = Block.blocksList[bId].blockMaterial;
		if(material.blocksMovement() || material == Material.water || material == Material.lava || material == Material.portal)
		{
			return false;
		}
		return true;
	}

	protected int getLargerQuanta(IBlockAccess world, int x, int y, int z, int compare)
	{
		int quantaRemaining = getQuantaValue(world, x, y, z);

		if(quantaRemaining <= 0)
		{
			return compare;
		}
		return quantaRemaining >= compare ? quantaRemaining : compare;
	}

}
