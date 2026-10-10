package com.hyrulecraft.util;

import com.hyrulecraft.item.HeartItem;
import com.hyrulecraft.item.HyruleShieldItem;
import com.hyrulecraft.magic.TimeRewind;
import com.hyrulecraft.registry.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

/** Eventos globales del servidor. */
public final class HyruleEvents {
    private HyruleEvents() {}

    private static final Set<String> RUPEE_MOBS = Set.of("zombie", "skeleton", "spider", "creeper", "husk", "stray",
            "drowned", "witch", "pillager", "vindicator", "cave_spider", "enderman");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            HookshotManager.tick(server);
            TimeRewind.tick(server);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            HookshotManager.clear(handler.player.getUuid());
            TimeRewind.forget(handler.player.getUuid());
        });

        // Los corazones extra sobreviven a la muerte.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            double bonus = HeartItem.bonus(oldPlayer);
            if (bonus > 0) {
                HeartItem.apply(newPlayer, bonus);
                newPlayer.setHealth(newPlayer.getMaxHealth());
            }
        });

        // Escudo Espejo: refleja proyectiles.
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof PlayerEntity p && p.isBlocking() && source.getSource() instanceof ProjectileEntity proj
                    && p.getActiveItem().getItem() instanceof HyruleShieldItem shield && shield.reflects()) {
                Vec3d look = p.getRotationVec(1.0F);
                proj.setVelocity(look.x, look.y, look.z, 1.6F, 0.0F);
                proj.setOwner(p);
                return false;
            }
            return true;
        });

        // Los monstruos clásicos sueltan rupias.
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin() || !id.getNamespace().equals("minecraft") || !id.getPath().startsWith("entities/")) return;
            if (!RUPEE_MOBS.contains(id.getPath().substring("entities/".length()))) return;
            tableBuilder.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(ModItems.RUPEE_GREEN))
                    .conditionally(RandomChanceLootCondition.builder(0.4F)).build());
            tableBuilder.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(ModItems.RUPEE_BLUE))
                    .conditionally(RandomChanceLootCondition.builder(0.08F)).build());
        });
    }
}
