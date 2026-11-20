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

package me.fallenbreath.tweakermore.mixins.tweaks.features.villagerOfferUsesDisplay;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import me.fallenbreath.tweakermore.util.Messenger;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
public abstract class MerchantScreenWidgetButtonPageMixin extends Button
{
	@SuppressWarnings("target")
	@Shadow @Final
	MerchantScreen this$0;

	@Shadow @Final int index;

	public MerchantScreenWidgetButtonPageMixin(
			int x, int y, int width, int height,
			Component message,
			OnPress onPress

			, CreateNarration createNarration
	)
	{
		super(
				x, y, width, height, message, onPress
				, createNarration
		);
	}

	@Inject(
			method = "extractToolTip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen$TradeOfferButton;getX()I",
					ordinal = 0
			)
	)
	private void renderMaxUsesAmount(
			GuiGraphicsExtractor context,
			int mouseX, int mouseY, CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.VILLAGER_OFFER_USES_DISPLAY.getBooleanValue())
		{
			int x =
					this.getX();
			if (x + 50 <= mouseX && mouseX <= x + 65)
			{
				MerchantScreen merchantScreen = this.this$0;

				MerchantOffer offer = merchantScreen.getMenu().getOffers().get(this.index + ((MerchantScreenAccessor)merchantScreen).getIndexStartOffset());

				String text = String.format("%d / %d", offer.getUses(), offer.getMaxUses());

				context.setTooltipForNextFrame(((ScreenAccessor)merchantScreen).getTextRenderer(), Messenger.s(text), mouseX, mouseY);
			}
		}
	}
}
