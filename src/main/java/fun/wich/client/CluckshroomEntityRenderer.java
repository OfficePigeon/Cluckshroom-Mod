package fun.wich.client;

import com.google.common.collect.Maps;
import fun.wich.CluckshroomEntity;
import fun.wich.CluckshroomMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CluckshroomEntityRenderer extends MobEntityRenderer<CluckshroomEntity, CluckshroomEntityRenderState, CluckshroomEntityModel> {
	protected final CluckshroomEntityModel adultModel;
	protected final CluckshroomEntityModel babyModel;
	private static final Map<CluckshroomEntity.Variant, Identifier> TEXTURES = Util.make(Maps.newHashMap(), (map) -> {
		map.put(CluckshroomEntity.Variant.BROWN, Identifier.of(CluckshroomMod.MOD_ID, "textures/entity/cluckshroom/cluckshroom_brown.png"));
		map.put(CluckshroomEntity.Variant.RED, Identifier.of(CluckshroomMod.MOD_ID, "textures/entity/cluckshroom/cluckshroom_red.png"));
	});
	public CluckshroomEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CluckshroomEntityModel(context.getPart(ModClient.CLUCKSHROOM)), 0.3F);
		this.adultModel = this.model;
		this.babyModel = new CluckshroomEntityModel(context.getPart(ModClient.CLUCKSHROOM_BABY));
	}

	public void render(CluckshroomEntityRenderState chickenEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
		this.model = chickenEntityRenderState.baby ? babyModel : adultModel;
		super.render(chickenEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
	}

	public Identifier getTexture(CluckshroomEntityRenderState chickenEntityRenderState) {
		return TEXTURES.get(chickenEntityRenderState.type);
	}

	public CluckshroomEntityRenderState createRenderState() {
		return new CluckshroomEntityRenderState();
	}

	public void updateRenderState(CluckshroomEntity chickenEntity, CluckshroomEntityRenderState chickenEntityRenderState, float f) {
		super.updateRenderState(chickenEntity, chickenEntityRenderState, f);
		chickenEntityRenderState.flapProgress = MathHelper.lerp(f, chickenEntity.lastFlapProgress, chickenEntity.flapProgress);
		chickenEntityRenderState.maxWingDeviation = MathHelper.lerp(f, chickenEntity.lastMaxWingDeviation, chickenEntity.maxWingDeviation);
		chickenEntityRenderState.type = chickenEntity.getVariant();
	}
}