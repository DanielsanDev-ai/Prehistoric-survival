package com.prehistoricsurvival.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.client.model.DodoModel;
import com.prehistoricsurvival.entity.DodoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DodoRenderer extends MobRenderer<DodoEntity, DodoModel> {
    private static final String[] VARIANTS = {"brown", "grey", "sand"};
    private static final ResourceLocation[][] TEXTURES = new ResourceLocation[VARIANTS.length][2];
    private static final ResourceLocation ALPHA_TEXTURE = tex("dodo_alpha");

    static {
        for (int v = 0; v < VARIANTS.length; v++) {
            TEXTURES[v][0] = tex("dodo_" + VARIANTS[v] + "_male");
            TEXTURES[v][1] = tex("dodo_" + VARIANTS[v] + "_female");
        }
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(PrehistoricSurvival.MOD_ID, "textures/entity/dodo/" + name + ".png");
    }

    public DodoRenderer(EntityRendererProvider.Context context) {
        super(context, new DodoModel(context.bakeLayer(DodoModel.LAYER)), 0.45f);
    }

    @Override
    public ResourceLocation getTextureLocation(DodoEntity entity) {
        if (entity.isAlpha()) return ALPHA_TEXTURE;
        int variant = Math.max(0, Math.min(entity.getVariant(), VARIANTS.length - 1));
        return TEXTURES[variant][entity.isFemale() ? 1 : 0];
    }

    @Override
    protected void scale(DodoEntity entity, PoseStack poseStack, float partialTick) {
        float s = entity.getBodyScale();
        poseStack.scale(s, s, s);
        this.shadowRadius = 0.45f * s;
    }

    /** Nombre, nivel y vida solo se ven de cerca (o mirando a la criatura hasta 32 bloques). */
    @Override
    protected boolean shouldShowName(DodoEntity entity) {
        double distance = this.entityRenderDispatcher.distanceToSqr(entity);
        return distance < 16.0 * 16.0
                || (distance < 32.0 * 32.0 && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }
}
