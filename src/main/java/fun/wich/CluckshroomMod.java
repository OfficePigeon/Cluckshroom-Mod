package fun.wich;

import fun.wich.mixin.Cluckshrooms_LootTablesMixin;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

import java.util.function.Function;

public class CluckshroomMod implements ModInitializer {
	public static final String MOD_ID = "wich";

	public static final SoundEvent ENTITY_CLUCKSHROOM_AMBIENT = register("entity.cluckshroom.ambient");
	public static final SoundEvent ENTITY_CLUCKSHROOM_DEATH = register("entity.cluckshroom.death");
	public static final SoundEvent ENTITY_CLUCKSHROOM_EGG = register("entity.cluckshroom.egg");
	public static final SoundEvent ENTITY_CLUCKSHROOM_MUSHROOM = register("entity.cluckshroom.mushroom");
	public static final SoundEvent ENTITY_CLUCKSHROOM_HURT = register("entity.cluckshroom.hurt");
	public static final SoundEvent ENTITY_CLUCKSHROOM_STEP = register("entity.cluckshroom.step");
	public static final SoundEvent ENTITY_CLUCKSHROOM_CONVERT = register("entity.cluckshroom.convert");
	public static final SoundEvent ENTITY_CLUCKSHROOM_SHEAR = register("entity.cluckshroom.shear");
	private static SoundEvent register(String path) {
		Identifier id = Identifier.of(MOD_ID, path);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}
	public static final RegistryKey<LootTable> CLUCKSHROOM_LAY_GAMEPLAY = registerLootTable("gameplay/cluckshroom_lay");
	public static final RegistryKey<LootTable> CLUCKSHROOM_SHEARING = registerLootTable("shearing/cluckshroom");
	private static RegistryKey<LootTable> registerLootTable(String name) {
		return Cluckshrooms_LootTablesMixin.registerLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, name)));
	}
	public static final TagKey<Item> TAG_CLUCKSHROOM_FOOD = TagKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "cluckshroom_food"));
	public static final TagKey<Block> TAG_CLUCKSHROOMS_SPAWNABLE_ON = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "cluckshrooms_spawnable_on"));
	public static final EntityType<CluckshroomEntity> CLUCKSHROOM = register(
			"cluckshroom",
			EntityType.Builder.create(CluckshroomEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.4F, 0.7F)
					.eyeHeight(0.644F)
					.passengerAttachments(new Vec3d(0.0F, 0.7, -0.1))
					.maxTrackingRange(10)
	);
	public static final EntityType<CluckshroomEggEntity> CLUCKSHROOM_EGG = register(
			"cluckshroom_egg",
			EntityType.Builder.<CluckshroomEggEntity>create(CluckshroomEggEntity::new, SpawnGroup.MISC)
					.dropsNothing()
					.dimensions(0.25F, 0.25F)
					.maxTrackingRange(4)
					.trackingTickInterval(10)
	);
	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> type) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, name));
		return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
	}
	public static final Item RED_CLUCKSHROOM_EGG = register("red_cluckshroom_egg", CluckshroomEggItem::new, new Item.Settings().maxCount(16));
	public static final Item BROWN_CLUCKSHROOM_EGG = register("brown_cluckshroom_egg", CluckshroomEggItem::new, new Item.Settings().maxCount(16));
	public static final Item CLUCKSHROOM_SPAWN_EGG = register("cluckshroom_spawn_egg", SpawnEggItem::new, new Item.Settings().spawnEgg(CLUCKSHROOM));
	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
		return Registry.register(Registries.ITEM, key, itemFactory.apply(settings.registryKey(key)));
	}
	@Override
	public void onInitialize() {
		//Attributes
		FabricDefaultAttributeRegistry.register(CLUCKSHROOM, CluckshroomEntity.createCluckshroomAttributes());
		//Spawning
		SpawnRestriction.register(CLUCKSHROOM, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CluckshroomEntity::canSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.MUSHROOM_FIELDS),
				SpawnGroup.CREATURE, CLUCKSHROOM, 6, 2, 6);
		//Items
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(itemGroup -> itemGroup.add(CLUCKSHROOM_SPAWN_EGG));
		//Eggs
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(itemGroup -> {
			itemGroup.add(RED_CLUCKSHROOM_EGG);
			itemGroup.add(BROWN_CLUCKSHROOM_EGG);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> {
			itemGroup.add(RED_CLUCKSHROOM_EGG);
			itemGroup.add(BROWN_CLUCKSHROOM_EGG);
		});
		DispenserBlock.registerProjectileBehavior(RED_CLUCKSHROOM_EGG);
		DispenserBlock.registerProjectileBehavior(BROWN_CLUCKSHROOM_EGG);
	}
}