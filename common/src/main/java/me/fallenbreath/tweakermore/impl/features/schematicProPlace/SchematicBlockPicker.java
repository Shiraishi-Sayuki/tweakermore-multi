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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.InventoryUtils;
import fi.dy.masa.litematica.util.ItemUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.LayerRange;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.litematica.config.Configs;
import net.minecraft.world.inventory.Slot;

public class SchematicBlockPicker
{
	private static final boolean TWEAKEROO_LOADED = FabricUtils.isModLoaded(ModIds.tweakeroo);

	/**
	 * Stolen from {@link fi.dy.masa.litematica.util.WorldUtils#doSchematicWorldPickBlock}
	 */
	public static void doSchematicWorldPickBlock(Minecraft mc, BlockPos pos, InteractionHand hand)
	{
		// do nothing if schematic rendering is not enabled
		if (!LitematicaConfigAccess.ENABLE_RENDERING().getBooleanValue() || !LitematicaConfigAccess.ENABLE_SCHEMATIC_RENDERING().getBooleanValue())
		{
			return;
		}

		Level schematicWorld = SchematicWorldHandler.getSchematicWorld();
		Level clientWorld = mc.level;
		if (schematicWorld != null && mc.player != null && clientWorld != null && mc.gameMode != null)
		{
			LayerRange layerRange = DataManager.getRenderLayerRange();
			if (!layerRange.isPositionWithinRange(pos))
			{
				return;
			}
			BlockState state = schematicWorld.getBlockState(pos);
			ItemStack stack = ProPlaceUtils.getItemForState(state, schematicWorld, pos);

			if (!stack.isEmpty())
			{
				boolean alreadyHolding = !GuiBase.isCtrlDown() && fi.dy.masa.malilib.util.InventoryUtils.areStacksEqual(stack, mc.player.getMainHandItem());
				if (!alreadyHolding)
				{
					pickSchematicBlock(mc, pos, schematicWorld, stack);
				}

				// so hand restore works fine
				fixTweakerooHandRestoreState(hand);
			}
		}
	}

	private static void pickSchematicBlock(Minecraft mc, BlockPos pos, Level schematicWorld, ItemStack stack)
	{
		if (mc.player == null || mc.gameMode == null || mc.level == null)
		{
			return;
		}

		InventoryUtils.schematicWorldPickBlock(stack, pos, schematicWorld, mc);
	}

	private static void fixTweakerooHandRestoreState(InteractionHand hand)
	{
		if (TWEAKEROO_LOADED)
		{
			fi.dy.masa.tweakeroo.tweaks.PlacementTweaks.cacheStackInHand(hand);
		}
	}
}
