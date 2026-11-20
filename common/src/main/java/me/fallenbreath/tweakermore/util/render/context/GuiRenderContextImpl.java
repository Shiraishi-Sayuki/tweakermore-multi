/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.render.context;

import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

import me.fallenbreath.tweakermore.util.render.matrix.Joml3x2fMatrixStack;

import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;


public class GuiRenderContextImpl implements GuiRenderContext
{
	@NotNull
	private final GuiGraphicsExtractor drawContext;

	@NotNull
	private final IMatrixStack matrixStack;

	public GuiRenderContextImpl(@NotNull GuiGraphicsExtractor drawContext, @NotNull IMatrixStack matrixStack)
	{
		this.drawContext = drawContext;
		this.matrixStack = matrixStack;
	}

	public GuiRenderContextImpl(@NotNull GuiGraphicsExtractor drawContext)
	{
		this(
				drawContext,
				new Joml3x2fMatrixStack(drawContext.pose())
		);
	}


	@Override
	@NotNull
	public GuiGraphicsExtractor getGuiDrawer()
	{
		return this.drawContext;
	}

	@Override
	public void pushMatrix()
	{
		this.matrixStack.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		this.matrixStack.popMatrix();
	}

	@Override
	public void translate(double x, double y)
	{
		this.matrixStack.translate(x, y, 0);
	}

	@Override
	public void scale(double x, double y)
	{
		this.matrixStack.scale(x, y, 1);
	}

}
