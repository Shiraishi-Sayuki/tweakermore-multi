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

package me.fallenbreath.tweakermore.mixins.tweaks.features.shulkerTooltipHints;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxTooltipHints.ShulkerBoxToolTipEnhancer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.world.item.component.ItemContainerContents;

import net.minecraft.world.item.Item;

import net.minecraft.network.chat.MutableComponent;

@Mixin(
		ItemContainerContents.class
)
public abstract class ShulkerBoxBlockMixin
{
	@ModifyArg(
			method = "addToTooltip",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=item.container.item_count"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
					ordinal = 0
			)
	)
	private Object shulkerTooltipHints_addHints(
			Object textObj,
			@Local(
			) ItemStack stackInTheContainer
			, @Local(argsOnly = true) Item.TooltipContext context
	)
	{
		MutableComponent text = (MutableComponent)textObj;
		ShulkerBoxToolTipEnhancer.appendContentHints(
				context,
				stackInTheContainer, text
		);
		return textObj;
	}
}
