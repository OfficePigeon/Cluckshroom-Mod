package fun.wich.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;

import java.util.Set;

public class CluckshroomEntityModel extends ChickenEntityModel {
	protected final ModelPart body;
	protected final ModelPart body_mushroom;
	public static final ModelTransformer BABY_CLUCKSHROOM_TRANSFORMER = new BabyModelTransformer(false, 5.0F, 2.0F, 2.0F, 1.99F, 24.0F, Set.of("head", "beak", "red_thing", "head_mushroom_1", "head_mushroom_2"));
	public CluckshroomEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.body = root.getChild("body");
		this.body_mushroom = this.body.getChild("body_mushroom");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = getModelData();// new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		//Head Mushroom
		ModelPartData head = modelPartData.getChild("head");
		head.addChild("head_mushroom_2", ModelPartBuilder.create().uv(38, 14).cuboid(0, -4, -2.5F, 0, 4, 5), ModelTransform.of(0, -6, -0.5F, 0, 0.7854F, 0));
		head.addChild("head_mushroom_1", ModelPartBuilder.create().uv(38, 14).cuboid(0, -4, -2.5F, 0, 4, 5), ModelTransform.of(0, -6, -0.5F, 0, -0.7854F, 0));
		//Body Mushroom
		ModelPartData body = modelPartData.getChild("body");
		ModelPartData body_mushroom = body.addChild("body_mushroom", ModelPartBuilder.create(), ModelTransform.origin(0, 8, 0));
		body_mushroom.addChild("head_mushroom_2", ModelPartBuilder.create().uv(38, 9).cuboid(0, -2.5F, 0, 0, 5, 5), ModelTransform.of(0, -7.5F, 3, 0, 0, -0.7854F));
		body_mushroom.addChild("head_mushroom_1", ModelPartBuilder.create().uv(38, 9).cuboid(0, -2.5F, 0, 0, 5, 5), ModelTransform.of(0, -7.5F, 3, 0, 0, 0.7854F));
		return TexturedModelData.of(modelData, 64, 32);
	}
	@Override
	public void setAngles(ChickenEntityRenderState state) {
		super.setAngles(state);
		this.body_mushroom.visible = !state.baby;
	}
}
