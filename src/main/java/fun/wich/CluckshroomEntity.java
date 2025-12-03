package fun.wich;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;

import java.util.UUID;
import java.util.function.IntFunction;

public class CluckshroomEntity extends AnimalEntity implements Shearable {
	public static final EntityDimensions BABY_BASE_DIMENSIONS = CluckshroomMod.CLUCKSHROOM.getDimensions().scaled(0.5F).withEyeHeight(0.2975F);
	public static final TrackedData<Integer> VARIANT = DataTracker.registerData(CluckshroomEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final int MUTATION_CHANCE = 1024;
	public static final boolean DEFAULT_HAS_JOCKEY = false;
	public static final int EGG_DELAY = 6000;
	public static final int MUSHROOM_DELAY = 6000;
	public float flapProgress;
	public float maxWingDeviation;
	public float lastMaxWingDeviation;
	public float lastFlapProgress;
	public float flapSpeed = 1.0F;
	private float field_28639 = 1.0F;
	public int eggLayTime;
	public int mushroomPlantTime;
	public boolean hasJockey = DEFAULT_HAS_JOCKEY;
	private UUID lightningId;
	public CluckshroomEntity(EntityType<? extends CluckshroomEntity> entityType, World world) {
		super(entityType, world);
		this.eggLayTime = this.random.nextInt(EGG_DELAY) + EGG_DELAY;
		this.mushroomPlantTime = this.random.nextInt(MUSHROOM_DELAY) + MUSHROOM_DELAY;
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}
	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM) ? 10.0F : world.getPhototaxisFavor(pos);
	}
	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0F));
		this.goalSelector.add(3, new TemptGoal(this, 1.0F, (stack) -> stack.isIn(CluckshroomMod.TAG_CLUCKSHROOM_FOOD), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0F));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}
	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
	}
	public static DefaultAttributeContainer.Builder createCluckshroomAttributes() {
		return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 4.0F).add(EntityAttributes.MOVEMENT_SPEED, 0.25F);
	}
	public static boolean canSpawn(EntityType<CluckshroomEntity> ignoredType, WorldAccess world, SpawnReason ignoredSpawnReason, BlockPos pos, Random ignoredRandom) {
		return world.getBlockState(pos.down()).isIn(CluckshroomMod.TAG_CLUCKSHROOMS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}
	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		UUID uUID = lightning.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setVariant(this.getVariant() == Variant.RED ? Variant.BROWN : Variant.RED);
			this.lightningId = uUID;
			this.playSound(CluckshroomMod.ENTITY_CLUCKSHROOM_CONVERT, 2.0F, 1.0F);
		}
	}
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, getSlotForHand(hand));
			}
			return ActionResult.SUCCESS;
		}
		else return super.interactMob(player, hand);
	}
	@Override
	public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
		world.playSoundFromEntity(null, this, CluckshroomMod.ENTITY_CLUCKSHROOM_SHEAR, shearedSoundCategory, 1, 1);
		this.convertTo(EntityType.CHICKEN, EntityConversionContext.create(this, false, false), (chicken) -> {
			world.spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getBodyY(0.5F), this.getZ(), 1, 0, 0, 0, 0);
			this.forEachShearedItem(world, CluckshroomMod.CLUCKSHROOM_SHEARING, shears, (worldx, stack) -> {
				for (int i = 0; i < stack.getCount(); ++i) {
					worldx.spawnEntity(new ItemEntity(this.getEntityWorld(), this.getX(), this.getBodyY(1), this.getZ(), stack.copyWithCount(1)));
				}
			});
		});
	}
	@Override public boolean isShearable() { return this.isAlive() && !this.isBaby(); }
	@Override
	public void tickMovement() {
		super.tickMovement();
		this.lastFlapProgress = this.flapProgress;
		this.lastMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.isOnGround() && this.flapSpeed < 1.0F) this.flapSpeed = 1.0F;
		this.flapSpeed *= 0.9F;
		Vec3d vec3d = this.getVelocity();
		if (!this.isOnGround() && vec3d.y < (double)0.0F) this.setVelocity(vec3d.multiply(1.0F, 0.6, 1.0F));
		this.flapProgress += this.flapSpeed * 2.0F;
		World world = this.getEntityWorld();
		if (world instanceof ServerWorld serverWorld) {
			if (this.isAlive() && !this.isBaby() && !this.hasJockey()) {
				if (--this.eggLayTime <= 0) {
					if (this.forEachGiftedItem(serverWorld, CluckshroomMod.CLUCKSHROOM_LAY_GAMEPLAY, this::dropStack)) {
						this.playSound(CluckshroomMod.ENTITY_CLUCKSHROOM_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
						this.emitGameEvent(GameEvent.ENTITY_PLACE);
					}
					this.eggLayTime = this.random.nextInt(EGG_DELAY) + EGG_DELAY;
				}
				if (--this.mushroomPlantTime <= 0) {
					BlockPos pos = this.getBlockPos();
					BlockState mushroom = getVariant().getMushroomState();
					if (mushroom.canPlaceAt(world, pos)) {
						this.playSound(CluckshroomMod.ENTITY_CLUCKSHROOM_MUSHROOM, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
						world.setBlockState(pos, mushroom, Block.NOTIFY_LISTENERS);
						this.emitGameEvent(GameEvent.ENTITY_PLACE);
					}
					this.mushroomPlantTime = this.random.nextInt(MUSHROOM_DELAY) + MUSHROOM_DELAY;
				}
			}
		}
	}
	@Override protected boolean isFlappingWings() { return this.speed > this.field_28639; }
	@Override protected void addFlapEffects() { this.field_28639 = this.speed + this.maxWingDeviation / 2.0F; }
	@Override protected SoundEvent getAmbientSound() { return CluckshroomMod.ENTITY_CLUCKSHROOM_AMBIENT; }
	@Override protected SoundEvent getHurtSound(DamageSource source){ return CluckshroomMod.ENTITY_CLUCKSHROOM_HURT; }
	@Override protected SoundEvent getDeathSound(){ return CluckshroomMod.ENTITY_CLUCKSHROOM_DEATH; }
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(CluckshroomMod.ENTITY_CLUCKSHROOM_STEP, 0.15F, 1.0F);
	}
	@Override
	public CluckshroomEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		CluckshroomEntity entity = CluckshroomMod.CLUCKSHROOM.create(serverWorld, SpawnReason.BREEDING);
		if (entity != null) entity.setVariant(passiveEntity instanceof CluckshroomEntity other ? this.chooseBabyVariant(other) : getVariant());
		return entity;
	}
	protected Variant chooseBabyVariant(CluckshroomEntity other) {
		Variant variant = this.getVariant();
		Variant variant2 = other.getVariant();
		if (variant == variant2 && this.random.nextInt(MUTATION_CHANCE) == 0) return variant == Variant.BROWN ? Variant.RED : Variant.BROWN;
		else return this.random.nextBoolean() ? variant : variant2;
	}
	@Override public boolean isBreedingItem(ItemStack stack) { return stack.isIn(CluckshroomMod.TAG_CLUCKSHROOM_FOOD); }
	@Override
	protected int getExperienceToDrop(ServerWorld world) {
		return this.hasJockey() ? 10 : super.getExperienceToDrop(world);
	}
	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, Variant.DEFAULT.index);
	}
	@Override
	public void readCustomDataFromNbt(NbtCompound view) {
		super.readCustomDataFromNbt(view);
		this.hasJockey = view.contains("IsChickenJockey") && view.getBoolean("IsChickenJockey");
		if (view.contains("EggLayTime")) this.eggLayTime = view.getInt("EggLayTime");
		if (view.contains("MushroomPlantTime")) this.mushroomPlantTime = view.getInt("MushroomPlantTime");
		this.setVariant(Variant.fromIndex(view.contains("Type") ? view.getInt("Type") : 0));
	}
	@Override
	public void writeCustomDataToNbt(NbtCompound view) {
		super.writeCustomDataToNbt(view);
		view.putBoolean("IsChickenJockey", this.hasJockey);
		view.putInt("EggLayTime", this.eggLayTime);
		view.putInt("MushroomPlantTime", this.mushroomPlantTime);
		view.putInt("Type", this.getVariant().getIndex());
	}
	public void setVariant(Variant variant) { this.dataTracker.set(VARIANT, variant.index); }
	public Variant getVariant() { return Variant.fromIndex(this.dataTracker.get(VARIANT)); }
	@Override public boolean canImmediatelyDespawn(double distanceSquared) { return this.hasJockey(); }
	@Override
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		super.updatePassengerPosition(passenger, positionUpdater);
		if (passenger instanceof LivingEntity livingEntity) livingEntity.bodyYaw = this.bodyYaw;
	}
	public boolean hasJockey() { return this.hasJockey; }
	public void setHasJockey(boolean hasJockey) { this.hasJockey = hasJockey; }
	public enum Variant implements StringIdentifiable {
		RED("red", 0, Blocks.RED_MUSHROOM.getDefaultState()),
		BROWN("brown", 1, Blocks.BROWN_MUSHROOM.getDefaultState());
		public static final Variant DEFAULT = RED;
		private static final IntFunction<Variant> INDEX_MAPPER = ValueLists.createIdToValueFunction(Variant::getIndex, values(), ValueLists.OutOfBoundsHandling.CLAMP);
		private final String name;
		private final int index;
		private final BlockState mushroom;
		Variant(final String name, final int index, final BlockState mushroom) {
			this.name = name;
			this.index = index;
			this.mushroom = mushroom;
		}
		public BlockState getMushroomState() { return this.mushroom; }
		public String asString() { return this.name; }
		public int getIndex() { return this.index; }
		static Variant fromIndex(int index) { return INDEX_MAPPER.apply(index); }
	}
}
