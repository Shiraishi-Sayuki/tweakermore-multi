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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.nameTagRenderStrategy;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.RestrictionType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import me.fallenbreath.tweakermore.impl.mc_tweaks.nameTagRenderStrategy.PlayerEntityRenderStateWithPlayerProfileName;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
		<T extends Entity, S extends EntityRenderState>
{
	@Inject(
			method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void nameTagRenderStrategy_impl(
			CallbackInfo ci,
			@Local(argsOnly = true) S entityState
	)
	{
		RestrictionType strategyType = (RestrictionType)TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE.getOptionListValue();
		if (
				strategyType != RestrictionType.NONE &&
				entityState instanceof AvatarRenderState playerEntityRenderState
		)
		{
			String playerName = ((PlayerEntityRenderStateWithPlayerProfileName)playerEntityRenderState).getPlayerProfileName$TKM();
			if (playerName == null)
			{
				// not a player (mc1.21.9+)
				return;
			}

			List<String> list = TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_LIST.getStrings();
			boolean shouldRender = strategyType.testEquality(playerName, list);
			if (!shouldRender)
			{
				ci.cancel();
			}
		}
	}
}
