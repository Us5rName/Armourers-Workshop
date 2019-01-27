package moe.plushie.armourers_workshop.common.inventory;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperty;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerOutfitMaker extends ModTileContainer<TileEntityOutfitMaker> implements IButtonPress {

    public ContainerOutfitMaker(EntityPlayer entityPlayer, TileEntityOutfitMaker tileEntity) {
        super(entityPlayer.inventory, tileEntity);
        
        
        ISkinType[] skinTypes = new ISkinType[] {
                SkinTypeRegistry.skinHead,
                SkinTypeRegistry.skinChest,
                SkinTypeRegistry.skinLegs,
                SkinTypeRegistry.skinFeet,
                SkinTypeRegistry.skinWings};
        
        addPlayerSlots(8, 132);
        
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinOutfit, tileEntity, 0, 8, 52));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 148, 52));
        for (int skinIndex = 0; skinIndex < skinTypes.length; skinIndex++) {
            for (int i = 0; i < TileEntityOutfitMaker.OUTFIT_ROWS; i++) {
                addSlotToContainer(new SlotSkin(skinTypes[skinIndex], tileEntity, skinIndex + (i * TileEntityOutfitMaker.OUTFIT_SKINS) + 2, 36 + skinIndex * 20, 22 + i * 20));
            }
        }
    }
    
    private void createOutfit() {
        ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();
        SkinProperties skinProperties = new SkinProperties();
        int[] paintData = null;
        for (int i = 2; i < tileEntity.getSizeInventory(); i++) {
            ItemStack stack = tileEntity.getStackInSlot(i);
            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            if (descriptor != null) {
                Skin skin = CommonSkinCache.INSTANCE.getSkin(descriptor);
                if (skin != null) {
                    skinParts.addAll(skin.getParts());
                    for (ISkinProperty prop : skin.getSkinType().getProperties()) {
                        SkinProperty p = (SkinProperty) prop;
                        p.setValue(skinProperties, p.getValue(skin.getProperties()));
                    }
                    if (skin.hasPaintData()) {
                        paintData = skin.getPaintData();
                    }
                }
            }
        }
        if (!skinParts.isEmpty()) {
            Skin skin = new Skin(skinProperties, SkinTypeRegistry.skinOutfit, paintData, skinParts);
            CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile)null);
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skin));
            tileEntity.setInventorySlotContents(1, skinStack);
        }
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 1) {
            createOutfit();
        }
    }
}
