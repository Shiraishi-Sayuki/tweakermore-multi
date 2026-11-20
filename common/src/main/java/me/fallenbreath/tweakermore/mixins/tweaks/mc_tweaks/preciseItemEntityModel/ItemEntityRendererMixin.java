/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.preciseItemEntityModel;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel.PreciseItemEntityModelUtils;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
	@ModifyExpressionValue(
			method = "renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;getRenderedAmount(I)I"
			)
	)
	private
	static
	int preciseItemEntityModel_setRenderedAmountTo1(int amount)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			amount = 1;
		}
		return amount;
	}

	@ModifyArg(
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
					ordinal = 0
			),
			index = 1
	)
	private float preciseItemEntityModel_adjustHeightShiftTo0(float y)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			y = 0;
		}
		return y;
	}

	@ModifyArg(
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;",
				ordinal = 0
			),
			index = 0
	)
	private float preciseItemEntityModel_tweakItemEntityRotation(
			float rotation,
			@Local(argsOnly = true) ItemEntity itemEntity,
			@Local(
					argsOnly = true,
					ordinal = 1
			)
			float tickDelta
	)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			rotation = 0;
			if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL_YAW_SNAP.getBooleanValue())
			{
				float yaw = itemEntity.getViewYRot(tickDelta);
				yaw = (yaw + 360) % 360;
				for (int r : new int[]{0, 90, 180, 270, 360})
				{
					if (r - 45 <= yaw && yaw < r + 45)
					{
						rotation = -r;
						rotation *= (float)(Math.PI / 180);
						break;
					}
				}
			}
		}
		return rotation;
	}

	@Inject(
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V"
			)
	)
	private void preciseItemEntityModel_transformOverrideOn(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			PreciseItemEntityModelUtils.transformOverrideFlag.set(true);
		}
	}

	@Inject(
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V",
					shift = At.Shift.AFTER
			)
	)
	private void preciseItemEntityModel_transformOverrideOff(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			PreciseItemEntityModelUtils.transformOverrideFlag.set(false);
		}
	}

}
