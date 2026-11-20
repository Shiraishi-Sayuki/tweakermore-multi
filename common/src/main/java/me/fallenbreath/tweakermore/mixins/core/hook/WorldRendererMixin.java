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

package me.fallenbreath.tweakermore.mixins.core.hook;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.event.TweakerMoreRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.joml.Matrix4fStack;

/**
 * mc1.19.4-: subproject 1.15.2 (main project)
 * mc1.19.4+: subproject 1.19.4        <--------
 */
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
	// around the renderChunkDebugInfo method call, before the matrixStack.pop() (matrixStack == RenderSystem.getModelViewStack())

	@Inject(
			method = "render",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;",
					remap = false,
					ordinal = 0
			)
	)
	private void worldRenderPostHook$TKM(
			CallbackInfo ci,
			@Local(
			)
			Matrix4fStack matrices
	)
	{
		TweakerMoreRenderEvents.dispatchRenderWorldPostEvent(
				Minecraft.getInstance(),
				matrices
		);
	}
}
