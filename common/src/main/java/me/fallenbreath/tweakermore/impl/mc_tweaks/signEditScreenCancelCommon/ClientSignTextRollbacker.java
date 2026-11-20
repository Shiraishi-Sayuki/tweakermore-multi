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

package me.fallenbreath.tweakermore.impl.mc_tweaks.signEditScreenCancelCommon;

import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenCancelCommon.SignBlockEntityAccessor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.network.chat.Component;


import net.minecraft.world.level.block.entity.SignText;

import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;

import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.signEditScreenCancelCommon.SignEditScreenAccessor;

@SuppressWarnings("FieldCanBeLocal")
public class ClientSignTextRollbacker
{
	private final SignBlockEntity signBlockEntity;

	private final SignText blockEntityFrontText;
	private final SignText blockEntityBackText;

	private final AbstractSignEditScreen signEditScreen;
	private final String[] screenTexts;

	public ClientSignTextRollbacker(
			AbstractSignEditScreen signEditScreen,
			SignBlockEntity signBlockEntity
	)
	{
		this.signEditScreen = signEditScreen;
		this.signBlockEntity = signBlockEntity;

		this.blockEntityFrontText = this.signBlockEntity.getText(true);
		this.blockEntityBackText = this.signBlockEntity.getText(false);

		this.screenTexts = ((SignEditScreenAccessor)this.signEditScreen).getTexts$TKM().clone();
	}

	public void rollback()
	{
		this.signBlockEntity.setText(this.blockEntityFrontText, true);
		this.signBlockEntity.setText(this.blockEntityBackText, false);

		final int lineNum = 4;
		for (int i = 0; i < lineNum; i++)
		{

			((SignEditScreenAccessor)this.signEditScreen).getTexts$TKM()[i] = this.screenTexts[i];
		}
	}
}
