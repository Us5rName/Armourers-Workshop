package moe.plushie.armourers_workshop.common.inventory;

import java.awt.Point;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
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
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerOutfitMaker extends ModTileContainer<TileEntityOutfitMaker> implements IButtonPress {

    private int indexSkinsStart = 0;
    private int indexSkinsEnd = 0;
    
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
        indexSkinsStart = getPlayerInvEndIndex() + 2;
        indexSkinsEnd = indexSkinsStart;
        for (int skinIndex = 0; skinIndex < skinTypes.length; skinIndex++) {
            for (int i = 0; i < TileEntityOutfitMaker.OUTFIT_ROWS; i++) {
                addSlotToContainer(new SlotSkin(skinTypes[skinIndex], tileEntity, skinIndex + (i * TileEntityOutfitMaker.OUTFIT_SKINS) + 2, 36 + skinIndex * 20, 22 + i * 20));
                indexSkinsEnd++;
            }
        }
    }
    
    private void loadOutfit() {
        
    }
    
    private void saveOutfit() {
        ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();
        SkinProperties skinProperties = new SkinProperties();
        String partIndexs = "";
        int[] paintData = null;
        for (int i = 2; i < tileEntity.getSizeInventory(); i++) {
            ItemStack stack = tileEntity.getStackInSlot(i);
            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            if (descriptor != null) {
                Skin skin = CommonSkinCache.INSTANCE.getSkin(descriptor);
                if (skin != null) {
                    for (int partIndex = 0; partIndex < skin.getPartCount(); partIndex++) {
                        SkinPart part = skin.getParts().get(partIndex);
                        skinParts.add(part);
                        /*if (skin.hasPaintData()) {
                            if (paintData == null) {
                                paintData = skin.getPaintData().clone();
                            } else {
                                ModLogger.log(part.getPartType().getRegistryName());
                                if (part.getPartType() instanceof ISkinPartTypeTextured) {
                                    ISkinPartTypeTextured texType = ((ISkinPartTypeTextured)part.getPartType());
                                    paintPart(texType, paintData, skin.getPaintData());
                                }
                            }
                        }*/
                    }

                    if (skin.hasPaintData()) {
                        ModLogger.log(skin.getPaintData().length);
                        if (paintData == null) {
                            paintData = new int[64 * 32];
                        }
                        
                        for (int j = 0 ; j < paintData.length; j++) {
                            
                        }
                        for (int partIndex = 0; partIndex < skin.getSkinType().getSkinParts().size(); partIndex++) {
                            ISkinPartType part = skin.getSkinType().getSkinParts().get(partIndex);
                            if (part instanceof ISkinPartTypeTextured) {
                                ModLogger.log(part.getRegistryName());
                                ISkinPartTypeTextured texType = ((ISkinPartTypeTextured) part);
                                paintData = paintPart(texType, paintData, skin.getPaintData());
                            }
                        }
                    }

                    if (partIndexs.isEmpty()) {
                        partIndexs = String.valueOf(skinParts.size());
                    } else {
                        partIndexs += ":" + String.valueOf(skinParts.size());
                    }
                    for (ISkinProperty prop : skin.getSkinType().getProperties()) {
                        SkinProperty p = (SkinProperty) prop;
                        p.setValue(skinProperties, p.getValue(skin.getProperties()));
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
    
    private int[] paintPart(ISkinPartTypeTextured texType, int[] desPaint, int[] srcPaint) {
        int textureWidth = 64;
        int textureHeight = 32;
        
        Point pos = texType.getTextureSkinPos();
        int width = (texType.getTextureModelSize().getX() * 2) + (texType.getTextureModelSize().getZ() * 2);
        int height = texType.getTextureModelSize().getY() + texType.getTextureModelSize().getZ();
        
        ModLogger.log("x:" + pos.x + " y:" + pos.y + " w:" + width + " height:" + height);
        
        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                int x = pos.x + ix;
                int y = pos.y + iy;
                desPaint[x + (y * textureWidth)] = srcPaint[x + (y * textureWidth)];
            }
        }
        return desPaint;
        /*for (int rgb = 0; rgb < paintData.length; rgb++) {
            byte[] trgb = PaintingHelper.intToBytes(skin.getPaintData()[rgb]);
            int type = (trgb[3] & 0xFF);
            //ModLogger.log(type);
            if (type != 0) {
                paintData[rgb] = skin.getPaintData()[rgb];
            }
        }*/
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            boolean slotted = false;
            
            // Putting skin in inv.
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = indexSkinsStart; i < indexSkinsEnd; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }
            
            // Putting outfit in input slot.
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = getPlayerInvEndIndex(); i < getPlayerInvEndIndex() + 1; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }

            if (!slotted) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, stack);

            return result;

        }
        return ItemStack.EMPTY;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 0) {
            loadOutfit();
        }
        if (buttonId == 1) {
            saveOutfit();
        }
    }
}
