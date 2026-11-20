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

package me.fallenbreath.tweakermore.util;

import com.sayuki.malilib.platform.Services;
import com.sayuki.malilib.util.PlatformUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FabricUtils
{
	public static boolean isModLoaded(String modId)
	{
		return Services.PLATFORM.isModLoaded(modId);
	}

	public static boolean isDevelopmentEnvironment()
	{
		return PlatformUtils.isDevelopmentEnvironment();
	}

	public static boolean doesModFitsAnyPredicate(String modId, Collection<String> versionPredicates)
	{
		if (!isModLoaded(modId))
		{
			return false;
		}

		String versionString = PlatformUtils.getModVersion(modId);
		if (versionPredicates.isEmpty())
		{
			return true;
		}

		for (String versionPredicate : versionPredicates)
		{
			if (doesVersionSatisfyPredicate(versionString, versionPredicate))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean doesVersionSatisfyPredicate(String versionString, String versionPredicate)
	{
		try
		{
			return VersionPredicate.parse(versionPredicate).test(versionString);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to parse version or version predicate {} {}: {}", versionString, versionPredicate, e.toString());
			return false;
		}
	}

	/**
	 * A minimal re-implementation of fabric-loader's version predicates,
	 * so it works on every mod loader.
	 */
	private static class VersionPredicate
	{
		private final List<Term> terms;

		private VersionPredicate(List<Term> terms)
		{
			this.terms = terms;
		}

		private static VersionPredicate parse(String predicate)
		{
			List<Term> terms = new ArrayList<>();
			for (String part : predicate.trim().split("\\s+"))
			{
				if (part.isEmpty())
				{
					continue;
				}
				String operator = "=";
				for (String candidate : new String[]{">=", "<=", ">", "<", "=", "~", "^"})
				{
					if (part.startsWith(candidate))
					{
						operator = candidate;
						part = part.substring(candidate.length()).trim();
						break;
					}
				}
				terms.add(new Term(operator, part));
			}
			if (terms.isEmpty())
			{
				throw new IllegalArgumentException("Empty version predicate");
			}
			return new VersionPredicate(terms);
		}

		private boolean test(String versionString)
		{
			SemanticVersion version = SemanticVersion.parse(versionString);
			for (Term term : this.terms)
			{
				if (!term.test(version))
				{
					return false;
				}
			}
			return true;
		}
	}

	private static class Term
	{
		private final String operator;
		private final String versionString;
		private final SemanticVersion version;
		private final SemanticVersion upperBound;
		private boolean any;

		private Term(String operator, String versionString)
		{
			this.operator = operator;
			this.versionString = versionString;

			if (versionString.equals("*") || versionString.equalsIgnoreCase("x"))
			{
				this.any = true;
				this.version = null;
				this.upperBound = null;
				return;
			}

			this.any = false;
			String[] parts = versionString.split("\\.");
			int wildcardIndex = -1;
			for (int i = 0; i < parts.length; i++)
			{
				String p = parts[i];
				if (p.equalsIgnoreCase("x") || p.equals("*"))
				{
					if (wildcardIndex < 0)
					{
						wildcardIndex = i;
					}
				}
				else if (wildcardIndex >= 0 && !p.isEmpty())
				{
					throw new IllegalArgumentException("Invalid wildcard version " + versionString);
				}
			}

			if (wildcardIndex == 0)
			{
				// e.g. "x.2" can't be meaningfully bounded, treat as "any"
				this.any = true;
				this.version = null;
				this.upperBound = null;
				return;
			}

			if (wildcardIndex < 0)
			{
				this.version = SemanticVersion.parse(versionString);
				this.upperBound = null;
			}
			else
			{
				// e.g. "1.2.x" -> [1, 2, 0] <= version < [1, 3, 0]
				int[] lowerParts = new int[wildcardIndex];
				for (int i = 0; i < wildcardIndex; i++)
				{
					lowerParts[i] = Integer.parseInt(parts[i]);
				}
				this.version = new SemanticVersion(lowerParts, null);

				int[] upperParts = new int[wildcardIndex + 1];
				System.arraycopy(lowerParts, 0, upperParts, 0, wildcardIndex);
				upperParts[wildcardIndex - 1] += 1;
				this.upperBound = new SemanticVersion(upperParts, null);
			}
		}

		private boolean test(SemanticVersion target)
		{
			if (this.any)
			{
				return true;
			}

			int cmp = target.compareTo(this.version);
			switch (this.operator)
			{
				case ">=":
					return cmp >= 0;
				case "<=":
					return cmp <= 0;
				case ">":
					return cmp > 0;
				case "<":
					return cmp < 0;
				case "=":
					if (this.upperBound != null)
					{
						return cmp >= 0 && target.compareTo(this.upperBound) < 0;
					}
					return cmp == 0;
				case "~":
				{
					int[] upper = this.version.components.clone();
					if (upper.length > 1)
					{
						upper[1] += 1;
						for (int i = 2; i < upper.length; i++)
						{
							upper[i] = 0;
						}
					}
					else
					{
						upper[0] += 1;
					}
					return cmp >= 0 && target.compareTo(new SemanticVersion(upper, null)) < 0;
				}
				case "^":
				{
					int[] upper = this.version.components.clone();
					int index = 0;
					while (index < upper.length - 1 && upper[index] == 0)
					{
						index++;
					}
					upper[index] += 1;
					for (int i = index + 1; i < upper.length; i++)
					{
						upper[i] = 0;
					}
					return cmp >= 0 && target.compareTo(new SemanticVersion(upper, null)) < 0;
				}
				default:
					throw new IllegalStateException("Unknown operator " + this.operator + " for " + this.versionString);
			}
		}
	}

	private static class SemanticVersion implements Comparable<SemanticVersion>
	{
		private final int[] components;
		private final String[] preRelease;

		private SemanticVersion(int[] components, String[] preRelease)
		{
			this.components = components;
			this.preRelease = preRelease;
		}

		private static SemanticVersion parse(String versionString)
		{
			String version = versionString.trim();

			int plusIndex = version.indexOf('+');
			if (plusIndex >= 0)
			{
				version = version.substring(0, plusIndex);
			}

			String preReleaseString = null;
			int dashIndex = version.indexOf('-');
			if (dashIndex >= 0)
			{
				preReleaseString = version.substring(dashIndex + 1);
				version = version.substring(0, dashIndex);
			}

			if (version.isEmpty())
			{
				version = "0";
			}

			String[] parts = version.split("\\.");
			int[] components = new int[parts.length];
			for (int i = 0; i < parts.length; i++)
			{
				String part = parts[i];
				// tolerate something like "1.21.1-pre1" leftovers or non-numeric suffixes
				int end = 0;
				while (end < part.length() && Character.isDigit(part.charAt(end)))
				{
					end++;
				}
				components[i] = end == 0 ? 0 : Integer.parseInt(part.substring(0, end));
			}

			String[] preRelease = preReleaseString == null ? null : preReleaseString.split("\\.");
			return new SemanticVersion(components, preRelease);
		}

		@Override
		public int compareTo(SemanticVersion other)
		{
			int length = Math.max(this.components.length, other.components.length);
			for (int i = 0; i < length; i++)
			{
				int a = i < this.components.length ? this.components[i] : 0;
				int b = i < other.components.length ? other.components[i] : 0;
				if (a != b)
				{
					return Integer.compare(a, b);
				}
			}

			if (this.preRelease == null && other.preRelease == null)
			{
				return 0;
			}
			if (this.preRelease == null)
			{
				return 1;
			}
			if (other.preRelease == null)
			{
				return -1;
			}

			int preLength = Math.max(this.preRelease.length, other.preRelease.length);
			for (int i = 0; i < preLength; i++)
			{
				if (i >= this.preRelease.length)
				{
					return -1;
				}
				if (i >= other.preRelease.length)
				{
					return 1;
				}
				String a = this.preRelease[i];
				String b = other.preRelease[i];
				boolean aNumeric = isNumeric(a);
				boolean bNumeric = isNumeric(b);
				int cmp;
				if (aNumeric && bNumeric)
				{
					cmp = Long.compare(Long.parseLong(a), Long.parseLong(b));
				}
				else if (aNumeric)
				{
					cmp = -1;
				}
				else if (bNumeric)
				{
					cmp = 1;
				}
				else
				{
					cmp = a.compareTo(b);
				}
				if (cmp != 0)
				{
					return cmp;
				}
			}
			return 0;
		}

		private static boolean isNumeric(String s)
		{
			if (s.isEmpty())
			{
				return false;
			}
			for (int i = 0; i < s.length(); i++)
			{
				if (!Character.isDigit(s.charAt(i)))
				{
					return false;
				}
			}
			return true;
		}
	}
}
