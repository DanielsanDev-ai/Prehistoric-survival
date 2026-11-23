package com.prehistoricsurvival.client.renderer;

import com.prehistoricsurvival.client.model.TheropodModel;
import com.prehistoricsurvival.client.model.TheropodSpecs;
import com.prehistoricsurvival.entity.TyrannosaurusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TyrannosaurusRenderer extends DinoRenderer<TyrannosaurusEntity, TheropodModel<TyrannosaurusEntity>> {
    public TyrannosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new TheropodModel<>(context.bakeLayer(TheropodModel.REX_LAYER), TheropodSpecs.TREX),
                0.9f, "tyrannosaurus", new String[] {"forest", "dusk", "ash"});
    }
}
