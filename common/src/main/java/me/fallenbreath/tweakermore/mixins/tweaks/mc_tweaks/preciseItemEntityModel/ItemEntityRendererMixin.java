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

import me.fallenbreath.tweakermore.impl.mc_tweaks.preciseItemEntityModel.ItemEntityRenderStateExtra;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{

	@ModifyArg(
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
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
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/math/Axis;rotation(F)Lorg/joml/Quaternionf;",
				ordinal = 0
			),
			index = 0
	)
	private float preciseItemEntityModel_tweakItemEntityRotation(
			float rotation,
			@Local(argsOnly = true) ItemEntityRenderState itemEntityRenderState
	)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			rotation = 0;
			if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL_YAW_SNAP.getBooleanValue())
			{
				float yaw = 0;
				if (itemEntityRenderState instanceof ItemEntityRenderStateExtra itemEntityRenderStateExtra)
				{
					yaw = itemEntityRenderStateExtra.getEntityYaw$TKM();
				}
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
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V"
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
			method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V",
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

	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V",
			at = @At("TAIL")
	)
	private void preciseItemEntityModel_storeTheYawInfo(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float tickDelta, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.PRECISE_ITEM_ENTITY_MODEL.getBooleanValue())
		{
			if (itemEntityRenderState instanceof ItemEntityRenderStateExtra itemEntityRenderStateExtra)
			{
				itemEntityRenderStateExtra.setEntityYaw$TKM(itemEntity.getYRot(tickDelta));
			}
		}
	}
}
