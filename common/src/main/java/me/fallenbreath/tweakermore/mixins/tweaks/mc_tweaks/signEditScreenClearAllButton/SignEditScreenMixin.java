/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenClearAllButton;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;


@Mixin(
		AbstractSignEditScreen.class
)
public abstract class SignEditScreenMixin extends Screen
{
	@Unique private static final String CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM = "tweakermore.gui.sign_edit_screen.clear_all";
	@Unique private static final String CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM = "tweakermore.gui.sign_edit_screen.clear_all_hover";
	@Unique private Button clearAllButton$TKM;

	@Unique private boolean clearAllButtonNeedsUnfocus$TKM;

	protected SignEditScreenMixin(Component title)
	{
		super(title);
	}

	@Shadow @Final private SignBlockEntity sign;
	@Shadow private int line;
	@Shadow private TextFieldHelper signField;

	@Shadow @Final private String[] messages;

	@Shadow protected abstract void setMessage(String string);

	@Inject(method = "init", at = @At("TAIL"))
	private void signEditScreenClearAllButton_addClearAllButton(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.SIGN_EDIT_SCREEN_CLEAR_ALL_BUTTON.getBooleanValue())
		{
			int y = this.height / 4 + 120;
			if (!TweakerMoreConfigs.SIGN_EDIT_SCREEN_CANCEL_BUTTON.getBooleanValue())
			{
				y += 24;
			}

			this.clearAllButton$TKM = Button.builder(Messenger.tr(CLEAR_ALL_BUTTON_TRANSLATION_KEY$TKM), button -> this.clearAllLines$TKM())
					.tooltip(Tooltip.create(Messenger.tr(CLEAR_ALL_BUTTON_HOVER_TRANSLATION_KEY$TKM)))
					.bounds(this.width / 2 + 105, y, 20, 20)
					.build();
			this.addRenderableWidget(this.clearAllButton$TKM);
		}
	}

	@Unique
	private void clearAllLines$TKM()
	{
		for (this.line = 0; this.line < this.messages.length; this.line++)
		{
			this.setMessage("");
		}
		this.line = 0;

		this.signField.setCursorToEnd();

		this.clearAllButtonNeedsUnfocus$TKM = true;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void signEditScreenClearAllButton_unfocusAfterPress(CallbackInfo ci)
	{
		if (this.clearAllButtonNeedsUnfocus$TKM && this.getFocused() == this.clearAllButton$TKM)
		{
			this.setFocused(null);
		}
		this.clearAllButtonNeedsUnfocus$TKM = false;
	}

}
