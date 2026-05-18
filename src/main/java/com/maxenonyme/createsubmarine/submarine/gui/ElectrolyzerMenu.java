package com.maxenonyme.createsubmarine.submarine.gui;

import com.maxenonyme.createsubmarine.CreateSubmarine;
import com.maxenonyme.createsubmarine.submarine.block.entity.ElectrolyzerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class ElectrolyzerMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    private final ContainerData data;

    public ElectrolyzerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, extraData.readBlockPos(), new SimpleContainerData(8));
    }

    public ElectrolyzerMenu(int id, Inventory inv, BlockPos pos, ContainerData data) {
        super(CreateSubmarine.ELECTROLYZER_MENU.get(), id);
        this.pos = pos;
        this.data = data;
        addDataSlots(data);
    }

    public ElectrolyzerMenu(int id, Inventory inv, ElectrolyzerBlockEntity be, ContainerData data) {
        this(id, inv, be.getBlockPos(), data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public int getEnergy() { return data.get(0); }
    public int getMaxEnergy() { return data.get(1); }
    public int getWater() { return data.get(2); }
    public int getMaxWater() { return data.get(3); }
    public int getOxygen() { return data.get(4); }
    public int getMaxOxygen() { return data.get(5); }
    public boolean isMachineEnabled() { return data.get(6) == 1; }
}