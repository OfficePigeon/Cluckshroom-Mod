package fun.wich;

import net.minecraft.entity.*;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class CluckshroomEggEntity extends ThrownItemEntity {
	protected static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0, 0);
	public CluckshroomEggEntity(EntityType<? extends CluckshroomEggEntity> entityType, World world) { super(entityType, world); }
	public CluckshroomEggEntity(World world, LivingEntity owner, ItemStack stack) {
		super(CluckshroomMod.CLUCKSHROOM_EGG, owner, world, stack);
	}
	public CluckshroomEggEntity(World world, double x, double y, double z, ItemStack stack) {
		super(CluckshroomMod.CLUCKSHROOM_EGG, x, y, z, world, stack);
	}
	public void handleStatus(byte status) {
		if (status == 3) {
			for (int i = 0; i < 8; ++i) {
				this.getEntityWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08);
			}
		}
	}
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 0);
	}
	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getEntityWorld().isClient()) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) i = 4;
				for (int j = 0; j < i; j++) {
					CluckshroomEntity entity = CluckshroomMod.CLUCKSHROOM.create(this.getEntityWorld(), SpawnReason.TRIGGERED);
					if (entity != null) {
						entity.setBreedingAge(-24000);
						entity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0);
						ItemStack stack = this.getStack();
						if (stack.isOf(CluckshroomMod.RED_CLUCKSHROOM_EGG)) entity.setVariant(CluckshroomEntity.Variant.RED);
						else if (stack.isOf(CluckshroomMod.BROWN_CLUCKSHROOM_EGG)) entity.setVariant(CluckshroomEntity.Variant.BROWN);
						if (!entity.recalculateDimensions(EMPTY_DIMENSIONS)) break;
						this.getEntityWorld().spawnEntity(entity);
					}
				}
			}
			this.getEntityWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
			this.discard();
		}
	}
	protected Item getDefaultItem() { return CluckshroomMod.RED_CLUCKSHROOM_EGG; }
}
