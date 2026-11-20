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

package me.fallenbreath.tweakermore.util.damage;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;

import java.util.Objects;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.world.level.ServerExplosion;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import java.util.List;

import net.minecraft.tags.DamageTypeTags;

@SuppressWarnings("UnusedReturnValue")
public class DamageCalculator
{
	private final LivingEntity entity;
	private float damageAmount;
	private final DamageSource damageSource;

	private ServerLevel serverWorld = null;

	private ApplyStage currentStage;

	private DamageCalculator(LivingEntity entity, float damageAmount, DamageSource damageSource)
	{
		this.entity = entity;
		this.damageAmount = damageAmount;
		this.damageSource = damageSource;
		this.currentStage = ApplyStage.NONE;
	}

	// mc1.21+ EPF calculation needs this
	public void setServerWorld(ServerLevel serverWorld)
	{
		this.serverWorld = serverWorld;
	}

	/*
	 * ============================
	 *          Factories
	 * ============================
	 */

	public static DamageCalculator create(LivingEntity entity, float damageAmount, DamageSource damageSource)
	{
		return new DamageCalculator(entity, damageAmount, damageSource);
	}

	public static DamageCalculator explosion(Vec3 explosionCenter, float explosionPower, LivingEntity entity)
	{
		float exposure =
				ServerExplosion.getSeenPercent
						(explosionCenter, entity);
		float maxRange = explosionPower * 2.0F;
		double distanceRatio = Math.sqrt(entity.distanceToSqr(explosionCenter)) / (double) maxRange;
		int damage;
		if (distanceRatio <= 1.0)
		{
			double damageFactor = (1.0 - distanceRatio) * exposure;
			damage = (int)((damageFactor * damageFactor + damageFactor) / 2.0 * 7.0 * maxRange + 1.0);
		}
		else
		{
			damage = 0;
		}
		return new DamageCalculator(
				entity, damage,
				entity.damageSources().badRespawnPointExplosion(explosionCenter)
		);
	}

	/*
	 * ============================
	 *           Getters
	 * ============================
	 */

	public LivingEntity getEntity()
	{
		return this.entity;
	}

	public float getEntityHealthAfterDeal()
	{
		return Math.max(this.entity.getHealth() - this.damageAmount, 0.0F);
	}

	public float getDamageAmount()
	{
		return this.damageAmount;
	}

	public DamageSource getDamageSource()
	{
		return this.damageSource;
	}

	/*
	 * ============================
	 *           Modifiers
	 * ============================
	 */

	private void checkAndSetStage(ApplyStage stage)
	{
		if (stage.ordinal() <= this.currentStage.ordinal())
		{
			TweakerMoreMod.LOGGER.warn("DamageCalculator wrong apply order: current {}, applying {}", this.currentStage, stage);
		}
		this.currentStage = stage;
	}

	/**
	 * Reference: {@link net.minecraft.world.entity.player.Player#damage}
	 */
	public DamageCalculator applyDifficulty(Difficulty difficulty)
	{
		this.checkAndSetStage(ApplyStage.DIFFICULTY);
		if (this.entity instanceof Player)
		{
			if (this.damageSource.scalesWithDifficulty())
			{
				this.damageAmount = me.fallenbreath.tweakermore.util.damage.DamageUtil.modifyDamageForDifficulty(this.damageAmount, difficulty);
			}
		}
		return this;
	}

	public DamageCalculator applyArmorAndResistanceAndEnchantment()
	{
		this.checkAndSetStage(ApplyStage.ARMOR);
		float amount = this.damageAmount;

		// ref: net.minecraft.entity.LivingEntity#applyDamage
		{
			// ref: net.minecraft.entity.LivingEntity#applyArmorToDamage
			if (!(
					this.damageSource.is(DamageTypeTags.BYPASSES_ARMOR)
			))
			{
				amount = CombatRules.getDamageAfterAbsorb(
						this.entity,
						amount,
						this.damageSource,
						this.entity.getArmorValue(),

						(float)this.entity.
								getAttributeValue(Attributes.ARMOR_TOUGHNESS)
				);
			}

			// ref: net.minecraft.entity.LivingEntity#applyEnchantmentsToDamage (mc < 1.19)
			//      net.minecraft.entity.LivingEntity#modifyAppliedDamage (mc > 1.19)
			if (!(
					this.damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)
			))
			{
				if (this.entity.hasEffect(MobEffects.RESISTANCE) && this.damageSource !=
						this.entity.damageSources().fellOutOfWorld()
				)
				{
					int level = Objects.requireNonNull(this.entity.getEffect(MobEffects.RESISTANCE)).getAmplifier() + 1;
					amount = Math.max(amount * (1 - level / 5.0F), 0.0F);
				}

				//noinspection PointlessBooleanExpression
				if (amount > 0.0F && !(
						this.damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)
				))
				{
					float level = this.calculateProtectionEnchantmentAmount();
					if (level > 0)
					{
						amount = CombatRules.getDamageAfterMagicAbsorb(amount, level);
					}
				}
			}
		}

		this.damageAmount = amount;
		return this;
	}

	private static Iterable<ItemStack> getEntityAllArmorItems(LivingEntity entity)
	{
		List<ItemStack> armorItems = Lists.newArrayList();
		for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR)
		{
			armorItems.add(entity.getItemBySlot(equipmentSlot));
		}
		return armorItems;
	}

	private float calculateProtectionEnchantmentAmount()
	{
		if (this.serverWorld != null)
		{
			return EnchantmentHelper.getDamageProtection(this.serverWorld, this.entity, this.damageSource);
		}
		else
		{
			// it's impossible do this client-side (Enchantment#modifyDamageProtection requires a ServerLevel)
			// what we can do best, is to simulate the basic legacy vanilla behavior

			// reference: mc1.20.6 net.minecraft.enchantment.ProtectionEnchantment.getProtectionAmount
			var source = this.damageSource;
			if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			{
				return 0;
			}

			float epf = 0;
			for (var item : getEntityAllArmorItems(this.entity))
			{
				for (var enchantmentEntry : item.getEnchantments().entrySet())
				{
					var enchantment = enchantmentEntry.getKey().unwrapKey().orElse(null);
					if (enchantment == null)
					{
						continue;
					}
					int level = enchantmentEntry.getIntValue();
					if (enchantment == Enchantments.PROTECTION)
					{
						epf += level;
					}
					else if (enchantment == Enchantments.FIRE_PROTECTION && source.is(DamageTypeTags.IS_FIRE))
					{
						epf += level * 2;
					}
					else if (enchantment == Enchantments.FEATHER_FALLING && source.is(DamageTypeTags.IS_FALL))
					{
						epf += level * 3;
					}
					else if (enchantment == Enchantments.BLAST_PROTECTION && source.is(DamageTypeTags.IS_EXPLOSION))
					{
						epf += level * 2;
					}
					else if (enchantment == Enchantments.PROJECTILE_PROTECTION && source.is(DamageTypeTags.IS_PROJECTILE))
					{
						epf += level * 2;
					}
				}
			}
			return epf;
		}
	}

	public DamageCalculator applyAbsorption()
	{
		this.checkAndSetStage(ApplyStage.ABSORPTION);
		this.damageAmount = Math.max(this.damageAmount - this.entity.getAbsorptionAmount(), 0.0F);
		return this;
	}

	private enum ApplyStage
	{
		NONE,
		DIFFICULTY,
		ARMOR,
		ABSORPTION
	}
}
