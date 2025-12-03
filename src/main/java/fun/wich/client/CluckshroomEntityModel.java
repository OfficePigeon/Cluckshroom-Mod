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
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F), ModelTransform.pivot(0.0F, 15.0F, -4.0F));
		modelPartData.addChild("beak", ModelPartBuilder.create().uv(14, 0).cuboid(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F), ModelTransform.pivot(0.0F, 15.0F, -4.0F));
		modelPartData.addChild("red_thing", ModelPartBuilder.create().uv(14, 4).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F), ModelTransform.pivot(0.0F, 15.0F, -4.0F));
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F), ModelTransform.of(0.0F, 16.0F, 0.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 0).cuboid(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
		modelPartData.addChild("right_leg", modelPartBuilder, ModelTransform.pivot(-2.0F, 19.0F, 1.0F));
		modelPartData.addChild("left_leg", modelPartBuilder, ModelTransform.pivot(1.0F, 19.0F, 1.0F));
		modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(24, 13).cuboid(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), ModelTransform.pivot(-4.0F, 13.0F, 0.0F));
		modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(24, 13).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), ModelTransform.pivot(4.0F, 13.0F, 0.0F));
		//Head Mushroom
		ModelPartData head = modelPartData.getChild("head");
		head.addChild("head_mushroom_2", ModelPartBuilder.create().uv(38, 14).cuboid(0, -4, -2.5F, 0, 4, 5), ModelTransform.of(0, -6, -0.5F, 0, 0.7854F, 0));
		head.addChild("head_mushroom_1", ModelPartBuilder.create().uv(38, 14).cuboid(0, -4, -2.5F, 0, 4, 5), ModelTransform.of(0, -6, -0.5F, 0, -0.7854F, 0));
		//Body Mushroom
		ModelPartData body = modelPartData.getChild("body");
		ModelPartData body_mushroom = body.addChild("body_mushroom", ModelPartBuilder.create(), ModelTransform.pivot(0, 8, 0));
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
