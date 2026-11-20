/*
 * This file is part of the Pistorder project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Pistorder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pistorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pistorder.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.features.pistorder;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.pistorder.PistorderRenderer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0"))
@Mixin(
		BlockBehaviour.BlockStateBase.class
)
public abstract class AbstractBlockStateMixin
{
	@Inject(
			method = "useWithoutItem",
			at = @At("HEAD"),
			cancellable = true
	)
	private void tkmPistorder_onPlayerRightClickBlock(
			Level world,
			Player player,
			BlockHitResult hit,

			CallbackInfoReturnable<InteractionResult> cir
	)
	{
		if (!PistorderRenderer.getInstance().isEnabled())
		{
			return;
		}

		if (world.isClientSide())
		{
			InteractionResult result = PistorderRenderer.getInstance().
					onPlayerRightClickBlockWithMainHand(world, player, hit);

			boolean ok = result.consumesAction();

			if (ok && TweakerMoreConfigs.PISTORDER_SWING_HAND.getBooleanValue())
			{
				cir.setReturnValue(
						result
				);
			}
		}
	}
}
