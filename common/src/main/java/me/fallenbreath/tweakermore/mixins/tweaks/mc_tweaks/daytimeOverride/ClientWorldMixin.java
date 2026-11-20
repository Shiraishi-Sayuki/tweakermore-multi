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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.daytimeOverride;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Modify daytime here too,
 * so the logic used when the client received a time update packet can be reused by us (gamerule changing etc.)
 * <p>
 * mc <  26.1 : subproject 1.15.2 (main project)
 * mc >= 26.1 : subproject 26.1.2        <--------
 */
@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin
{
	@Inject(
			method = "setDayTime",
			at = @At("HEAD")
	)
	private void overwriteDayTime_modifySetter(
			CallbackInfo ci,
			@Local(argsOnly = true) LocalLongRef timeOfDay
	)
	{
		if (TweakerMoreConfigs.DAYTIME_OVERRIDE.getBooleanValue())
		{
			timeOfDay.set(-TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue());
		}
	}
}
