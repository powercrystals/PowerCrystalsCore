package powercrystals.core.position;

import net.minecraftforge.common.ForgeDirection;

public interface IRotateableTile
{
	public boolean canRotate();
	public void rotate();
	public ForgeDirection getDirectionFacing();
}
