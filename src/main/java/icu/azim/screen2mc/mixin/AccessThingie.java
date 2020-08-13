package icu.azim.screen2mc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemFrameEntity.class)
public interface AccessThingie {
	@Accessor("ITEM_STACK")
	TrackedData<ItemStack> getItemStack();
}
