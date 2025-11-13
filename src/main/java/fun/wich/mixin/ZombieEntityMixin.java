package fun.wich.mixin;

import fun.wich.CluckshroomEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
	protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}
	@Inject(method="initialize", at=@At("TAIL"))
	private void TryChickenJockeyingCluckshrooms(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
		if (!this.isBaby()) return;
		if (this.hasVehicle()) return;
		if (entityData instanceof ZombieEntity.ZombieData zombieData && zombieData.tryChickenJockey) {
			if (random.nextFloat() < 0.05) {
				List<CluckshroomEntity> list = world.getEntitiesByClass(CluckshroomEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
				if (!list.isEmpty()) {
					CluckshroomEntity chickenEntity = list.getFirst();
					chickenEntity.setHasJockey(true);
					this.startRiding(chickenEntity, false, false);
				}
			}
		}
	}
}
