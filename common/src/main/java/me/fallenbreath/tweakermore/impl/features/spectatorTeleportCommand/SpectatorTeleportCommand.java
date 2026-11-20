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

package me.fallenbreath.tweakermore.impl.features.spectatorTeleportCommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sayuki.tweakermore.command.CommandSourceAdapter;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.Messenger;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;

import java.util.UUID;

import static net.minecraft.commands.arguments.EntityArgument.entity;

public class SpectatorTeleportCommand
{
	public static <S> void register(CommandDispatcher<S> dispatcher, CommandSourceAdapter<S> adapter)
	{
		String prefix = TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND_PREFIX.getStringValue();
		LiteralArgumentBuilder<S> root = adapter.literal(prefix)
				.requires(s -> TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND.getBooleanValue())
				.then(adapter.argument("target", entity())
						.executes(c -> doSpectatorTeleport(adapter, c.getSource(), getEntity(c, "target")))
				);
		dispatcher.register(root);
		TweakerMoreMod.LOGGER.debug("(spectatorTeleportCommand) Registered client-side command with prefix '{}'", prefix);
	}

	public static <S> UUID getEntity(CommandContext<S> context, String target) throws CommandSyntaxException
	{
		EntitySelector selector = context.getArgument(target, EntitySelector.class);
		return EntitySelectorHack.getSingleEntityUuid(selector);
	}

	private static <S> int doSpectatorTeleport(CommandSourceAdapter<S> adapter, S source, UUID target) throws CommandSyntaxException
	{
		LocalPlayer player = adapter.getPlayer(source);
		if (!player.isSpectator())
		{
			adapter.sendError(source, Messenger.tr("tweakermore.impl.spectatorTeleportCommand.need_spectator"));
			return 0;
		}

		TweakerMoreMod.LOGGER.info("Performing spectator teleport to entity {}", target);
		player.connection.send(new ServerboundTeleportToEntityPacket(target));
		return 1;
	}
}
