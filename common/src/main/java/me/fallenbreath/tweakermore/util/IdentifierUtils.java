/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util;

import net.minecraft.resources.Identifier;
import net.minecraft.IdentifierException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IdentifierUtils
{
	/**
	 * @throws IdentifierException if the identifier string is invalid
	 */
	public static Identifier of(String id)
	{
		return Identifier.parse(id);
	}

	/**
	 * @throws IdentifierException if the identifier string is invalid
	 */
	public static Identifier of(String namespace, String path)
	{
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static Optional<Identifier> tryParse(String id)
	{
		try
		{
			return Optional.of(of(id));
		}
		catch (IdentifierException e)
		{
			return Optional.empty();
		}
	}

	@Nullable
	public static Identifier tryParseOrNull(String id)
	{
		return tryParse(id).orElse(null);
	}
}
