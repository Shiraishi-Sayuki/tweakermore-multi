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

package me.fallenbreath.tweakermore.event;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.context.GuiRenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;


import org.joml.Matrix4fStack;

import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;

public abstract class TweakerMoreRenderEvents
{
	private static final List<TweakerMoreIRenderer> renderers = Lists.newArrayList();

	public static void register(TweakerMoreIRenderer renderer)
	{
		renderers.add(renderer);
	}

	public static void init()
	{
		RenderEventHandler.getInstance().registerGameOverlayRenderer(new MalilibRendererHook());
	}

	public static void dispatchRenderWorldPostEvent(
			Minecraft mc
			, Matrix4fStack matrixStack
	)
	{
		ProfilerFiller profiler = mc.getProfiler();

		profiler.popPush("TweakerMore_WorldRenderPostHook");
		WorldRenderContext renderContext = RenderContext.world(
				matrixStack
		);
		TextRenderer.beginBatch();
		try
		{
			renderers.forEach(renderer -> renderer.onRenderWorldLast(renderContext));
		}
		finally
		{
			try
			{
				TextRenderer.endBatch();
			}
			finally
			{
				profiler.pop();
			}
		}
	}

	private static void dispatchRenderGameOverlayPost(GuiRenderContext renderContext)
	{
		renderers.forEach(renderer -> renderer.onRenderGameOverlayPost(renderContext));
	}

	private static class MalilibRendererHook implements IRenderer
	{
		@Override
		public void onRenderGameOverlayPost(
				GuiGraphics matrixStackOrDrawContext
		)
		{
			dispatchRenderGameOverlayPost(
					RenderContext.gui(
							matrixStackOrDrawContext
					)
			);
		}
	}
}
