package com.dyxiaojiazi.elytra_renewed;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ElytraRenewedClient implements ClientModInitializer {
	/** 水平最大速度：30 格/秒 = 1.5 格/tick */
	private static final double MAX_SPEED = 1.5;
	/** 每 tick 加速度：约 2 格/秒² = 0.1 格/tick² */
	private static final double ACCELERATION = 0.1;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			LocalPlayer player = client.player;
			if (player == null) return;
			if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) return;
			if (!player.isFallFlying()) return;
			if (player.zza <= 0) return;

			// 饱食度 ≤ 6 时无法加速（与原版无法疾跑一致）
			if (player.getFoodData().getFoodLevel() <= 6) return;

			Vec3 look = player.getViewVector(1.0F);
			Vec3 forward = new Vec3(look.x, 0, look.z);
			if (forward.lengthSqr() < 1.0E-6) return;
			forward = forward.normalize();

			Vec3 motion = player.getDeltaMovement();
			double newX = motion.x + forward.x * ACCELERATION;
			double newZ = motion.z + forward.z * ACCELERATION;

			double horizSpeed = Math.sqrt(newX * newX + newZ * newZ);
			if (horizSpeed > MAX_SPEED) {
				newX = newX / horizSpeed * MAX_SPEED;
				newZ = newZ / horizSpeed * MAX_SPEED;
			}

			player.setDeltaMovement(newX, motion.y, newZ);
			player.hurtMarked = true;

			// 通知服务端扣饥饿等级
			ClientPlayNetworking.send(new ElytraRenewedNetworking.BoostPayload());
		});
	}
}