package com.dyxiaojiazi.elytra_renewed;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

public class ElytraRenewedNetworking {

    public static final CustomPacketPayload.Type<BoostPayload> BOOST_TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(
                    ElytraRenewed.MOD_ID, "boost"));

    public record BoostPayload() implements CustomPacketPayload {
        public static final StreamCodec<FriendlyByteBuf, BoostPayload> CODEC =
                StreamCodec.unit(new BoostPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return BOOST_TYPE;
        }
    }

    public static void register() {
        // ✅ 新名字：serverboundPlay()
        PayloadTypeRegistry.serverboundPlay().register(BOOST_TYPE, BoostPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BOOST_TYPE, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                if (!player.isFallFlying()) return;
                if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) return;

                // 约 16 秒扣 1 点饱食度：每 tick 0.0125 饥饿等级
                player.getFoodData().addExhaustion(0.0125f);
            });
        });
    }
}