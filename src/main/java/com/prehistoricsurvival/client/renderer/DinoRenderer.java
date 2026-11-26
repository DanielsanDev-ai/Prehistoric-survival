package com.prehistoricsurvival.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.entity.PrehistoricEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderizador comun: elige la textura por variante, genero y alfa, aplica el tamano real
 * (crecimiento x escala del modelo de la especie) y muestra nombre/nivel/vida solo de cerca.
 */
public class DinoRenderer<T extends PrehistoricEntity, M extends EntityModel<T>> extends MobRenderer<T, M> {
    private final String[] variants;
    private final ResourceLocation[][] textures;
    private final ResourceLocation alphaTexture;
    private final ResourceLocation glowTexture;
    private final float baseShadow;

    public DinoRenderer(EntityRendererProvider.Context context, M model, float shadow, String species, String[] variants) {
        super(context, model, shadow);
        this.variants = variants;
        this.baseShadow = shadow;
        this.textures = new ResourceLocation[variants.length][2];
        for (int v = 0; v < variants.length; v++) {
            textures[v][0] = tex(species, species + "_" + variants[v] + "_male");
            textures[v][1] = tex(species, species + "_" + variants[v] + "_female");
        }
        this.alphaTexture = tex(species, species + "_alpha");
        this.glowTexture = tex(species, species + "_alpha_glow");
        addLayer(new GlowLayer<>(this, e -> glowTexture));
    }

    private static ResourceLocation tex(String species, String name) {
        return new ResourceLocation(PrehistoricSurvival.MOD_ID, "textures/entity/" + species + "/" + name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.isAlpha()) return alphaTexture;
        int variant = Math.max(0, Math.min(entity.getVariant(), variants.length - 1));
        return textures[variant][entity.isFemale() ? 1 : 0];
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float s = entity.getBodyScale() * entity.species().modelScale();
        poseStack.scale(s, s, s);
        this.shadowRadius = baseShadow * s;
    }

    /** Nombre, nivel y vida solo se ven de cerca (o mirando a la criatura hasta 32 bloques). */
    @Override
    protected boolean shouldShowName(T entity) {
        double distance = this.entityRenderDispatcher.distanceToSqr(entity);
        return distance < 16.0 * 16.0
                || (distance < 32.0 * 32.0 && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }
}
