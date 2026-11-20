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

package me.fallenbreath.tweakermore.util.render;

import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

import org.joml.Matrix4f;

public class InWorldPositionTransformer
{
	private final Vec3 pos;
	private WorldRenderContext renderContext;

	public InWorldPositionTransformer(Vec3 pos)
	{
		this.pos = pos;
		this.renderContext = null;
	}

	/**
	 * Matrix stack of renderContext will be pushed
	 */
	public void apply(WorldRenderContext renderContext)
	{
		this.renderContext = renderContext;
		Minecraft client = Minecraft.getInstance();
		Camera camera = client.gameRenderer.getMainCamera();
		Vec3 vec3d = this.pos.subtract(camera.getPosition());

		renderContext.pushMatrix();
		renderContext.translate(vec3d.x, vec3d.y, vec3d.z);

		renderContext.multMatrix(
				new Matrix4f().rotation(camera.rotation())
		);
	}

	/**
	 * Matrix stack of renderContext will be popped
	 */
	public void restore()
	{
		Objects.requireNonNull(this.renderContext);
		this.renderContext.popMatrix();
		this.renderContext = null;
	}
}
