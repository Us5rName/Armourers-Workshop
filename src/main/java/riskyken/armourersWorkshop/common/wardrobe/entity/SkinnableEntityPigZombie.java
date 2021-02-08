package riskyken.armourersWorkshop.common.wardrobe.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.entity.SkinnableEntityZombieRenderer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

import java.util.ArrayList;

public class SkinnableEntityPigZombie implements ISkinnableEntity {

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityPigZombie.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends ISkinnableEntityRenderer> getRendererClass() { return SkinnableEntityZombieRenderer.class; }//Using the same renderer because Zombie Pigmen use the same shape as Zombies

    @Override
    public boolean canUseWandOfStyle() {
        return true;
    }

    @Override
    public boolean canUseSkinsOnEntity() {
        return false;
    }
    
    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinHead);
        skinTypes.add(SkinTypeRegistry.skinChest);
        skinTypes.add(SkinTypeRegistry.skinLegs);
        skinTypes.add(SkinTypeRegistry.skinFeet);
        skinTypes.add(SkinTypeRegistry.skinWings);
    }
}
