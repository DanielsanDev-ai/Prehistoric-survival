package com.prehistoricsurvival.client.renderer;

import com.prehistoricsurvival.client.model.TheropodModel;
import com.prehistoricsurvival.client.model.TheropodSpecs;
import com.prehistoricsurvival.entity.DilophosaurusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DilophosaurusRenderer extends DinoRenderer<DilophosaurusEntity, TheropodModel<DilophosaurusEntity>> {
    public DilophosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new TheropodModel<>(context.bakeLayer(TheropodModel.DILO_LAYER), TheropodSpecs.DILOPHOSAURUS),
                0.6f, "dilophosaurus", new String[] {"viridian", "amber", "teal"});
    }
}
