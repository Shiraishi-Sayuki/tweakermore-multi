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

package me.fallenbreath.tweakermore.impl.features.refreshInventory;

import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;

import net.minecraft.network.HashedStack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class InventoryRefresher
{
	/**
	 * Inspired by viaversion 1.14 -> 1.13 villager selection inventory forced resync
	 * https://github.com/ViaVersion/ViaVersion/blob/4074352a531cfb0de6fa81e043ee761737748a7a/common/src/main/java/com/viaversion/viaversion/protocols/protocol1_14to1_13_2/packets/InventoryPackets.java#L238
	 */
	public static void refresh()
	{
		Minecraft mc = Minecraft.getInstance();
		ClientPacketListener networkHandler = mc.getConnection();
		if (networkHandler != null && mc.player != null)
		{
			ItemStack uniqueItem = new ItemStack(Items.STONE);

			// Tags with NaN are not equal, so the server will find an inventory desync and send an inventory refresh to the client
			var nbt = new CompoundTag();
			nbt.putDouble("force_sync", Double.NaN);
			CustomData.set(DataComponents.CUSTOM_DATA, uniqueItem, nbt);

			AbstractContainerMenu csh = mc.player.containerMenu;
			HashedStack itemStackHash = HashedStack.create(uniqueItem, networkHandler.decoratedHashOpsGenenerator());
			networkHandler.send(new ServerboundContainerClickPacket(
					csh.containerId,
					csh.getStateId(),
					(short)-999, (byte)2,
					ContainerInput.QUICK_CRAFT,

					new Int2ObjectOpenHashMap<>()
					, itemStackHash
			));

			InfoUtils.printActionbarMessage("tweakermore.impl.refreshInventory.refreshed");
		}
	}
}
