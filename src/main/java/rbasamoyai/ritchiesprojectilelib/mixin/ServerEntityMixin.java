package rbasamoyai.ritchiesprojectilelib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.ritchiesprojectilelib.RPLTags;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundPreciseMotionSyncPacket;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

	@Shadow @Final private Entity entity;

	@Inject(method = "sendChanges", at = @At("HEAD"))
	private void ritchiesprojectilelib$sendChanges$0(CallbackInfo ci) {
		if (this.entity.getType().builtInRegistryHolder().is(RPLTags.PRECISE_MOTION)) {
			Vec3 pos = this.entity.position();
			Vec3 vel = this.entity.getDeltaMovement();
			RPLNetwork.sendToClientTracking(new ClientboundPreciseMotionSyncPacket(this.entity.getId(), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, this.entity.getYRot(), this.entity.getXRot(), this.entity.onGround(), 3), this.entity);
			this.entity.needsSync = false;
		}
	}

	@WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V"))
	private void ritchiesprojectilelib$sendChanges$suppressVanillaMotion(ServerEntity.Synchronizer instance, Packet<? super ClientGamePacketListener> packet, Operation<Void> original) {
		if (this.entity.getType().builtInRegistryHolder().is(RPLTags.PRECISE_MOTION)
				&& (packet instanceof ClientboundSetEntityMotionPacket
					|| packet instanceof ClientboundMoveEntityPacket
					|| packet instanceof ClientboundEntityPositionSyncPacket)) {
			return;
		}
		original.call(instance, packet);
	}

}
