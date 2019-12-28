package net.cerulan.aetherflow.mixin;

import net.cerulan.aetherflow.api.AetherNetworks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(ServerWorld.class)
public abstract class AetherNetworkWorldHooksMixin {

    @Inject(at = @At("HEAD"), method = "save")
    public void save(CallbackInfo info) {
        ServerWorld world = (ServerWorld) ((Object) this);
        CompoundTag tag = AetherNetworks.INSTANCE.toNBT(world, new CompoundTag());
        File file = world.getSaveHandler().getWorldDir();
        try {
            Path folder = file.toPath().resolve("aethernetworks");
            Files.createDirectories(folder);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(folder.resolve("dim_" + world.dimension.getType().getRawId() + ".dat").toFile()));
            tag.write(dos);
            dos.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    public void load(CallbackInfo info) {
        ServerWorld world = (ServerWorld) ((Object) this);
        File file = world.getSaveHandler().getWorldDir();
        try {
            Path folder = file.toPath().resolve("aethernetworks");
            Files.createDirectories(folder);
            Path nbtPath = folder.resolve("dim_" + world.dimension.getType().getRawId() + ".dat");
            if (Files.exists(nbtPath)) {
                DataInputStream dis = new DataInputStream(new FileInputStream(nbtPath.toFile()));
                CompoundTag tag = NbtIo.read(dis);
                dis.close();
                if (tag != null)
                    AetherNetworks.INSTANCE.fromNBT(world, tag);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
