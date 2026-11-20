package com.sayuki.tweakermore.neoforge;

import com.sayuki.tweakermore.command.TweakerMoreCommands;
import com.sayuki.tweakermore.neoforge.command.NeoForgeCommandSourceAdapter;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;

// NeoForge client entrypoint - クライアント限定で共通初期化とクライアントコマンド登録をやる
@Mod(value = TweakerMoreMod.MOD_ID, dist = Dist.CLIENT)
public class TweakerMoreNeoForge
{
	public TweakerMoreNeoForge(IEventBus modEventBus, ModContainer modContainer)
	{
		// 共通初期化 - Fabric側と同じ流れでFallen_Breathのコードを呼ぶ
		TweakerMoreMod.init();

		// 設定画面登録 - Mod一覧のボタンから開けるようにする
		Registry.CONFIG_SCREEN.registerConfigScreenFactory(new ModInfo(TweakerMoreMod.MOD_ID, TweakerMoreMod.MOD_NAME, TweakerMoreConfigGui::new));
		modContainer.registerExtensionPoint(IConfigScreenFactory.class,
				(java.util.function.Supplier<IConfigScreenFactory>) () -> (IConfigScreenFactory) (minecraft, parent) -> {
					TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
					gui.setParent(parent);
					return gui;
				});

		// クライアントコマンド登録 - ディスパッチャが作られたタイミングで登録する
		// RegisterClientCommandsEvent は Forge メインイベントバスで発火する
		NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
				TweakerMoreCommands.registerAll(event.getDispatcher(), NeoForgeCommandSourceAdapter.INSTANCE)
		);
	}
}
