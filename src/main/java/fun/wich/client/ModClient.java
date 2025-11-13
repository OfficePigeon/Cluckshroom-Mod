package fun.wich.client;

import fun.wich.CluckshroomMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
	public static final EntityModelLayer CLUCKSHROOM = MakeModelLayer("cluckshroom");
	public static final EntityModelLayer CLUCKSHROOM_BABY = MakeModelLayer("cluckshroom_baby");
	private static EntityModelLayer MakeModelLayer(String id) {
		return new EntityModelLayer(Identifier.of(CluckshroomMod.MOD_ID, id), "main");
	}
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(CLUCKSHROOM, CluckshroomEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(CLUCKSHROOM_BABY, () -> CluckshroomEntityModel.getTexturedModelData().transform(CluckshroomEntityModel.BABY_TRANSFORMER));
		EntityRendererFactories.register(CluckshroomMod.CLUCKSHROOM, CluckshroomEntityRenderer::new);
		EntityRendererFactories.register(CluckshroomMod.CLUCKSHROOM_EGG, FlyingItemEntityRenderer::new);
	}
}