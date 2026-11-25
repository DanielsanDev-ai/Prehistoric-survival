package com.prehistoricsurvival.client.renderer;

import com.prehistoricsurvival.client.model.DilophosaurusModel;
import com.prehistoricsurvival.entity.DilophosaurusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DilophosaurusRenderer extends DinoRenderer<DilophosaurusEntity, DilophosaurusModel> {
    public DilophosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new DilophosaurusModel(context.bakeLayer(DilophosaurusModel.LAYER)),
                0.55f, "dilophosaurus", new String[] {"marsh"});
    }
}
