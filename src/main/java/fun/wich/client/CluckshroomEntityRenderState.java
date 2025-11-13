package fun.wich.client;

import fun.wich.CluckshroomEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;

@Environment(EnvType.CLIENT)
public class CluckshroomEntityRenderState extends ChickenEntityRenderState {
	public CluckshroomEntity.Variant type;
}
