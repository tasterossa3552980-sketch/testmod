package com.test.testmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class LaunchTriggerPacket {

    public LaunchTriggerPacket() {}

    public static void encode(LaunchTriggerPacket msg, net.minecraft.network.FriendlyByteBuf buf) {}
    public static LaunchTriggerPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new LaunchTriggerPacket();
    }

    public static void handle(LaunchTriggerPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p = ctx.getSender();
            if (p == null) return;

            UUID alreadySelected = ModEvents.selectedForLaunch.get(p.getUUID());

            if (alreadySelected == null) {
                // 目前沒有選中任何目標 → 這次按鍵是「選中」動作
                ModEvents.selectFirstOrbiter(p);
            } else {
                // 已經有選中的目標了 → 這次按鍵是「真正發射」
                ModEvents.launchSelectedEntity(p);
            }
        });
        ctx.setPacketHandled(true);
    }
}
