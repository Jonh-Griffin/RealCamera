package com.xtracr.realcamera.compat;

import com.xtracr.realcamera.RealCameraCore;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import java.awt.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class CompatibilityHelper {
    private static Class<?> NEA_NEAnimationsLoader = null;
    private static Method NEA_playerTransformer_setDeltaTick = null;
    protected static Method Exposure_CameraItem_isActive = null;
    private static Class<?> GeckoLib_GeoRenderer = null;
    private static Method GeckoLib_GeoRenderer_getGeoModel = null;
    private static Method GeckoLib_GeoRenderer_getTextureLocation = null;
    private static Method GeckoLib_GeoRenderer_getRenderType = null;
    private static Method GeckoLib_GeoRenderer_getRenderColor = null;
    private static Method GeckoLib_GeoRenderer_getPackedOverlay = null;
    private static Method GeckoLib_GeoRenderer_reRender = null;
    private static Method GeckoLib_GeoModel_getModelResource = null;
    private static Method GeckoLib_GeoModel_getBakedModel = null;

    @SuppressWarnings("unchecked")
    public static void initialize() {
        if (isClassLoaded("net.bettercombat.BetterCombat")) try {
            Class<?> BetterCombat_CompatibilityFlags = Class.forName("net.bettercombat.compatibility.CompatibilityFlags");
            Field firstPersonRenderField = BetterCombat_CompatibilityFlags.getField("firstPersonRender");
            Supplier<Boolean> firstPersonRender = (Supplier<Boolean>) firstPersonRenderField.get(null);
            Supplier<Boolean> newFirstPersonRender = () -> firstPersonRender.get() && !RealCameraCore.isRendering();
            firstPersonRenderField.set(null, newFirstPersonRender);
        } catch (Exception ignored) {
        }
        if (isClassLoaded("dev.tr7zw.notenoughanimations.versionless.NEABaseMod")) try {
            NEA_NEAnimationsLoader = Class.forName("dev.tr7zw.notenoughanimations.NEAnimationsLoader");
            Class<?> NEA_PlayerTransformer = Class.forName("dev.tr7zw.notenoughanimations.logic.PlayerTransformer");
            NEA_playerTransformer_setDeltaTick = NEA_PlayerTransformer.getDeclaredMethod("setDeltaTick", float.class);
        } catch (Exception ignored) {
        }
        if (isClassLoaded("io.github.mortuusars.exposure.Exposure")) try {
            Class<?> Exposure_CameraItem = Class.forName("io.github.mortuusars.exposure.item.CameraItem");
            Exposure_CameraItem_isActive = Exposure_CameraItem.getDeclaredMethod("isActive", ItemStack.class);
        } catch (Exception ignored) {
        }
        if (isClassLoaded("software.bernie.geckolib.GeckoLib")) try {
            GeckoLib_GeoRenderer = Class.forName("software.bernie.geckolib.renderer.GeoRenderer");
            GeckoLib_GeoRenderer_getGeoModel = GeckoLib_GeoRenderer.getMethod("getGeoModel");
            GeckoLib_GeoRenderer_getTextureLocation = GeckoLib_GeoRenderer.getMethod("getTextureLocation", Object.class);
            GeckoLib_GeoRenderer_getRenderType = GeckoLib_GeoRenderer.getMethod("getRenderType", Object.class, Class.forName("net.minecraft.resources.ResourceLocation"), Class.forName("net.minecraft.client.renderer.MultiBufferSource"), float.class);
            GeckoLib_GeoRenderer_getRenderColor = GeckoLib_GeoRenderer.getMethod("getRenderColor", Object.class, float.class, int.class);
            GeckoLib_GeoRenderer_getPackedOverlay = GeckoLib_GeoRenderer.getMethod("getPackedOverlay", Object.class, float.class, float.class);
            GeckoLib_GeoRenderer_reRender = GeckoLib_GeoRenderer.getMethod("reRender", Class.forName("software.bernie.geckolib.cache.object.BakedGeoModel"), Class.forName("com.mojang.blaze3d.vertex.PoseStack"), Class.forName("net.minecraft.client.renderer.MultiBufferSource"), Object.class, Class.forName("net.minecraft.client.renderer.RenderType"), Class.forName("com.mojang.blaze3d.vertex.VertexConsumer"), float.class, int.class, int.class, float.class, float.class, float.class, float.class);
            Class<?> GeoModel = Class.forName("software.bernie.geckolib.model.GeoModel");
            GeckoLib_GeoModel_getModelResource = GeoModel.getMethod("getModelResource", Object.class, GeckoLib_GeoRenderer);
            GeckoLib_GeoModel_getBakedModel = GeoModel.getMethod("getBakedModel", Class.forName("net.minecraft.resources.ResourceLocation"));
        } catch (Exception ignored) {
        }
    }

    public static void NEA_setDeltaTick(float tickDelta) {
        if (NEA_NEAnimationsLoader != null) try {
            Object NEA_NEAnimationsLoader_INSTANCE = NEA_NEAnimationsLoader.getDeclaredField("INSTANCE").get(null);
            Object NEA_playerTransformer = NEA_NEAnimationsLoader.getDeclaredField("playerTransformer").get(NEA_NEAnimationsLoader_INSTANCE);
            NEA_playerTransformer_setDeltaTick.invoke(NEA_playerTransformer, tickDelta);
        } catch (Exception ignored) {
        }
    }

    public static boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean GeckoLib_render(Object renderer, Object entity, float tickDelta, Object poseStack, Object bufferSource, int packedLight) {
        if (GeckoLib_GeoRenderer != null && GeckoLib_GeoRenderer.isInstance(renderer)) try {
            Object model = GeckoLib_GeoRenderer_getGeoModel.invoke(renderer);
            Object modelResource = GeckoLib_GeoModel_getModelResource.invoke(model, entity, renderer);
            Object bakedModel = GeckoLib_GeoModel_getBakedModel.invoke(model, modelResource);
            Object texture = GeckoLib_GeoRenderer_getTextureLocation.invoke(renderer, entity);
            Object renderType = GeckoLib_GeoRenderer_getRenderType.invoke(renderer, entity, texture, bufferSource, tickDelta);
            Object color = GeckoLib_GeoRenderer_getRenderColor.invoke(renderer, entity, tickDelta, packedLight);
            float red = ((java.awt.Color) color).getRed() / 255f;
            float green = ((java.awt.Color) color).getGreen() / 255f;
            float blue = ((java.awt.Color) color).getBlue() / 255f;
            float alpha = ((java.awt.Color) color).getAlpha() / 255f;
            int overlay = (int) GeckoLib_GeoRenderer_getPackedOverlay.invoke(renderer, entity, 0f, tickDelta);
            GeckoLib_GeoRenderer_reRender.invoke(renderer, bakedModel, poseStack, bufferSource, entity, renderType, null, tickDelta, packedLight, overlay, red, green, blue, alpha);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }
}
