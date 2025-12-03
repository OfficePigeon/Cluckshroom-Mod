package fun.wich.client;

import fun.wich.CluckshroomMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CluckshroomClient implements ClientModInitializer {
	public static final EntityModelLayer CLUCKSHROOM = new EntityModelLayer(Identifier.of(CluckshroomMod.MOD_ID, "cluckshroom"), "main");
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(CLUCKSHROOM, CluckshroomEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(CluckshroomMod.CLUCKSHROOM, CluckshroomEntityRenderer::new);
		EntityRendererRegistry.register(CluckshroomMod.CLUCKSHROOM_EGG, FlyingItemEntityRenderer::new);
	}
}