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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.scoreboardSideBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphics;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(
		Gui.class
)
public abstract class InGameHudMixin
{

	@Unique
	@Nullable
	private RenderUtils.Scaler scaler = null;

	@ModifyVariable(
			method = "method_55440",  // lambda method as the context.draw() callback in method renderScoreboardSidebar
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Options;getBackgroundColor(F)I",
					ordinal = 0
			),
			ordinal = 3  // the lv that stores `this.scaledHeight / 2 + h / 3`
	)
	private int tweakerMore_scoreboardSideBarScale_push(
			int centerY
			, @Local(argsOnly = true) GuiGraphics matrixStackOrDrawContext
	)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.isModified())
		{
			int screenWidth = matrixStackOrDrawContext.guiWidth();
			this.scaler = RenderUtils.createScaler(screenWidth, centerY, TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.getDoubleValue());
			this.scaler.apply(RenderContext.gui(
					matrixStackOrDrawContext
			));
		}
		return centerY;
	}

	@Inject(
			method = "displayScoreboardSidebar",
			at = @At("RETURN")
	)
	private void tweakerMore_scoreboardSideBarScale_pop(CallbackInfo ci)
	{
		if (this.scaler != null)
		{
			this.scaler.restore();
		}
	}
}
