package com.sayuki.tweakermore.neoforge.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sayuki.tweakermore.command.CommandSourceAdapter;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

// NeoForge用のコマンドソースアダプタ - Brigadierの共通コマンド定義をNeoForgeのコマンドソースへ橋渡しする
public class NeoForgeCommandSourceAdapter implements CommandSourceAdapter<CommandSourceStack>
{
	public static final NeoForgeCommandSourceAdapter INSTANCE = new NeoForgeCommandSourceAdapter();

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> literal(String name)
	{
		return Commands.literal(name);
	}

	@Override
	public RequiredArgumentBuilder<CommandSourceStack, ?> argument(String name, ArgumentType<?> type)
	{
		return Commands.argument(name, type);
	}

	@Override
	public LocalPlayer getPlayer(CommandSourceStack source) throws CommandSyntaxException
	{
		// クライアントコマンドのsenderはローカルプレイヤーなのでMinecraftから直接取る
		LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
		if (player == null)
		{
			throw net.minecraft.commands.arguments.EntityArgument.NO_ENTITIES_FOUND.create();
		}
		return player;
	}

	@Override
	public void sendError(CommandSourceStack source, Component message)
	{
		source.sendFailure(message);
	}
}
