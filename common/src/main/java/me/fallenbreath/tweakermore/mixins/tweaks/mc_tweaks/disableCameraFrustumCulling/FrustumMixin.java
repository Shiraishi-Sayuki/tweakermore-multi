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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
public abstract class FrustumMixin implements CouldBeAlwaysVisibleFrustum
{
	@Unique
	private boolean alwaysVisible = false;

	@Inject(method = "<init>(Lnet/minecraft/client/renderer/culling/Frustum;)V", at = @At("TAIL"))
	private void disableCameraFrustumCulling_copyAlwaysVisibleFlagOnCtor(Frustum frustum, CallbackInfo ci)
	{
		this.alwaysVisible = ((FrustumMixin)(Object)frustum).alwaysVisible;
	}

	@Inject(method = "set", at = @At("TAIL"))
	private void disableCameraFrustumCulling_copyAlwaysVisibleFlagOnSet(Frustum frustum, CallbackInfo ci)
	{
		this.alwaysVisible = ((FrustumMixin)(Object)frustum).alwaysVisible;
	}

	@Override
	public void setAlwaysVisible$TKM(boolean alwaysVisible)
	{
		this.alwaysVisible = alwaysVisible;
	}

	@Override
	public boolean getAlwaysVisible$TKM()
	{
		return this.alwaysVisible;
	}

	@Inject(
			method = "isVisible",
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible_0(AABB box, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.alwaysVisible)
		{
			cir.setReturnValue(true);
		}
	}

	@Inject(
			method = "cubeInFrustum(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)I",
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible_1(BoundingBox box, CallbackInfoReturnable<Integer> cir)
	{
		if (this.alwaysVisible)
		{
			cir.setReturnValue(-2);
		}
	}

	@Inject(
			method = "pointInFrustum",
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible_2(double x, double y, double z, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.alwaysVisible)
		{
			cir.setReturnValue(true);
		}
	}
}
