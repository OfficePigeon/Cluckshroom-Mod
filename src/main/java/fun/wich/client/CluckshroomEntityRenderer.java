package fun.wich.client;

import com.google.common.collect.Maps;
import fun.wich.CluckshroomEntity;
import fun.wich.CluckshroomMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CluckshroomEntityRenderer extends MobEntityRenderer<CluckshroomEntity, ChickenEntityModel<CluckshroomEntity>> {
	private static final Map<CluckshroomEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
		map.put(CluckshroomEntity.Variant.BROWN, Identifier.of(CluckshroomMod.MOD_ID, "textures/entity/cluckshroom/cluckshroom_brown.png"));
		map.put(CluckshroomEntity.Variant.RED, Identifier.of(CluckshroomMod.MOD_ID, "textures/entity/cluckshroom/cluckshroom_red.png"));
	});
	public CluckshroomEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CluckshroomEntityModel(context.getPart(CluckshroomClient.CLUCKSHROOM)), 0.3F);
	}
	@Override public Identifier getTexture(CluckshroomEntity state) { return TEXTURES.get(state.getVariant()); }
	@Override
	protected float getAnimationProgress(CluckshroomEntity chickenEntity, float f) {
		float g = MathHelper.lerp(f, chickenEntity.lastFlapProgress, chickenEntity.flapProgress);
		float h = MathHelper.lerp(f, chickenEntity.lastMaxWingDeviation, chickenEntity.maxWingDeviation);
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}