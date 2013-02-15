package powercrystals.core.power;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerProvider;

public class PowerProviderAdvanced extends PowerProvider
{
    public PowerProviderAdvanced()
    {
        powerLoss = 0;
        powerLossRegularity = 72000;

        configure(0, 0);
    }

    public void configure(int maxEnergyReceived, int maxStoredEnergy)
    {
        super.configure(0, 0, maxEnergyReceived, 0, maxStoredEnergy);
    }

    @Override
    public boolean update(IPowerReceptor receptor)
    {
        return false;
    }

    @Override
    public void receiveEnergy(float quantity, ForgeDirection from)
    {
        energyStored += quantity;

        if (energyStored > maxEnergyStored)
        {
            energyStored = maxEnergyStored;
        }
    }
}