package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public abstract class ProjectileBurstRenderer<T extends ProjectileBurst> extends EntityRenderer<T, ProjectileBurstRenderer.BurstRenderState> {

	protected ProjectileBurstRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public BurstRenderState createRenderState() {
		return new BurstRenderState();
	}

	@Override
	public void extractRenderState(T entity, BurstRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.subProjectiles = List.copyOf(entity.getSubProjectiles());
		state.partialTick = partialTick;
	}

	@Override
	public void submit(BurstRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		for (ProjectileBurst.SubProjectile subProjectile : state.subProjectiles) {
			poseStack.pushPose();
			double[] disp = subProjectile.displacement();
			double[] vel = subProjectile.velocity();
			poseStack.translate(disp[0] + vel[0] * state.partialTick, disp[1] + vel[1] * state.partialTick, disp[2] + vel[2] * state.partialTick);
			this.renderSubProjectile(subProjectile, state.partialTick, poseStack, submitNodeCollector, state.lightCoords);
			poseStack.popPose();
		}
		super.submit(state, poseStack, submitNodeCollector, camera);
	}

	protected void renderSubProjectile(ProjectileBurst.SubProjectile subProjectile, float partialTick, PoseStack poseStack,
									   SubmitNodeCollector submitNodeCollector, int packedLight) {
	}

	public static class BurstRenderState extends EntityRenderState {
		public List<ProjectileBurst.SubProjectile> subProjectiles = List.of();
		public float partialTick;
	}

}
