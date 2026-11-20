package com.sayuki.tweakermore.fabric;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

// Fabric pre-launch entrypoint - fabric.mod.json の preLaunch から呼ばれる
// Fabric Loader 側でも MixinExtras を使えるようにブートストラップする
// (NeoForge は MixinExtras を内蔵しているので不要)
public class TweakerMorePreLaunch implements PreLaunchEntrypoint
{
	@Override
	public void onPreLaunch()
	{
		MixinExtrasBootstrap.init();
	}
}
