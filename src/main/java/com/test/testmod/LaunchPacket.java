package com.test.testmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class LaunchPacket {

    // 這個訊息不需要帶任何額外資料，單純當作「觸發訊號」
    public LaunchPacket() {
    }

    public static void encode(LaunchPacket msg, net.minecraft.network.FriendlyByteBuf buf) {
        // 沒有資料要打包，留空
    }

    public static LaunchPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new LaunchPacket();
    }

    public static void handle(LaunchPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p = ctx.getSender();
            if (p == null) return;

            Set<UUID> myOrbiters = ModEvents.orbitingEntities.get(p.getUUID());
            if (myOrbiters == null || myOrbiters.isEmpty()) return;

            ServerLevel serverLevel = p.serverLevel();
            Vec3 look = p.getLookAngle();
            Vec3 horizontalDirection = new Vec3(look.x, 0, look.z).normalize();

            double launchSpeed = 1.5;

            for (UUID id : myOrbiters) {
                Entity entity = serverLevel.getEntity(id);
                if (entity == null) continue;

                entity.setDeltaMovement(
                        horizontalDirection.x * launchSpeed,
                        0.1,   // 給一點點固定的微小上升力，避免直接貼地面滑行
                        horizontalDirection.z * launchSpeed
                );
                entity.hurtMarked = true;

                ModEvents.launchedEntities.put(entity.getUUID(), p.getUUID());   // 標記「這隻正在飛行中」
            }
            myOrbiters.clear();
        });
        ctx.setPacketHandled(true);
    }
}