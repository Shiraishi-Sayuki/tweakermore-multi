package com.sayuki.tweakermore.fabric.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.sayuki.tweakermore.command.CommandSourceAdapter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

// Fabric用のコマンドソースアダプタ - FabricのクライアントコマンドAPIを共通コードから使えるようにする
public class FabricCommandSourceAdapter implements CommandSourceAdapter<FabricClientCommandSource>
{
	public static final FabricCommandSourceAdapter INSTANCE = new FabricCommandSourceAdapter();

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> literal(String name)
	{
		return ClientCommands.literal(name);
	}

	@Override
	public RequiredArgumentBuilder<FabricClientCommandSource, ?> argument(String name, ArgumentType<?> type)
	{
		return ClientCommands.argument(name, type);
	}

	@Override
	public LocalPlayer getPlayer(FabricClientCommandSource source)
	{
		return source.getPlayer();
	}

	@Override
	public void sendError(FabricClientCommandSource source, Component message)
	{
		source.sendError(message);
	}
}
