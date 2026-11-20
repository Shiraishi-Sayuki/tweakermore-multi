package com.sayuki.tweakermore.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;

// ModMenu連携 - Mod一覧の「設定」ボタンからTweakerMoreの設定画面を開けるようにする
public class ModMenuImpl implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return (screen) -> {
			TweakerMoreConfigGui gui = new TweakerMoreConfigGui();
			gui.setParent(screen);
			return gui;
		};
	}
}
