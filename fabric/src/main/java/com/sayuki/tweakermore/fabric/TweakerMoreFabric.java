package com.sayuki.tweakermore.fabric;

import com.sayuki.tweakermore.command.TweakerMoreCommands;
import com.sayuki.tweakermore.fabric.command.FabricCommandSourceAdapter;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.FabricUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

// Fabric client entrypoint - fabric.mod.json の client から呼ばれる
// 共通初期化のあと、クライアントコマンドをディスパッチャへ登録する
public class TweakerMoreFabric implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		TweakerMoreMod.init();

		// コマンド登録 - コマンドAPIが無い環境では何もしない
		if (FabricUtils.isModLoaded("fabric-command-api-v2"))
		{
			ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
					TweakerMoreCommands.registerAll(dispatcher, FabricCommandSourceAdapter.INSTANCE)
			);
		}
		else
		{
			TweakerMoreMod.LOGGER.warn("fabric-command-api-v2 does not exist, client commands registration skipped");
		}
	}
}
