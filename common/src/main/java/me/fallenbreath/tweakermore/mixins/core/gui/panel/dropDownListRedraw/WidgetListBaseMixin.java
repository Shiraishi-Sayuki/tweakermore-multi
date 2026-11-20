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

package me.fallenbreath.tweakermore.mixins.core.gui.panel.dropDownListRedraw;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetListConfigOptionsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.render.GuiContext;

@Mixin(WidgetListBase.class)
public abstract class WidgetListBaseMixin<TYPE, WIDGET extends WidgetListEntryBase<TYPE>>
{
	// to make sure it only once gets rendered
	@Unique
	private boolean shouldRenderTweakerMoreConfigGuiDropDownList = false;

	@Inject(method = "drawContents", at = @At("HEAD"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListSetFlag(CallbackInfo ci)
	{
		shouldRenderTweakerMoreConfigGuiDropDownList = true;
	}


	@Inject(method = "drawContents", at = @At("TAIL"), remap = false)
	private void drawTweakerMoreConfigGuiDropDownListAgainAfterHover(
			GuiContext matrixStackOrDrawContext,
			int mouseX, int mouseY, float partialTicks, CallbackInfo ci
	)
	{
		this.drawTweakerMoreConfigGuiDropDownListAgain(
				matrixStackOrDrawContext,
				mouseX, mouseY
		);
	}

	@SuppressWarnings("ConstantConditions")
	@Unique
	private boolean isTweakerMoreConfigGui()
	{
		if ((WidgetListBase<?, ?>)(Object)this instanceof WidgetListConfigOptions)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();
			return guiConfig instanceof TweakerMoreConfigGui;
		}
		return false;
	}

	@Unique
	private void drawTweakerMoreConfigGuiDropDownListAgain(
			GuiContext matrixStackOrDrawContext,
			int mouseX, int mouseY
	)
	{
		if (this.isTweakerMoreConfigGui() && this.shouldRenderTweakerMoreConfigGuiDropDownList)
		{
			GuiConfigsBase guiConfig = ((WidgetListConfigOptionsAccessor) this).getParent();

			// render it again to make sure it's on the top but below hovering widgets
			((TweakerMoreConfigGui)guiConfig).renderDropDownList(
					matrixStackOrDrawContext,
					mouseX, mouseY
			);
			this.shouldRenderTweakerMoreConfigGuiDropDownList = false;
		}
	}
}
