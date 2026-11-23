package com.prehistoricsurvival.client.renderer;

import com.prehistoricsurvival.client.model.DodoModel;
import com.prehistoricsurvival.entity.DodoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DodoRenderer extends DinoRenderer<DodoEntity, DodoModel> {
    public DodoRenderer(EntityRendererProvider.Context context) {
        super(context, new DodoModel(context.bakeLayer(DodoModel.LAYER)), 0.45f, "dodo",
                new String[] {"brown", "grey", "sand"});
    }
}
