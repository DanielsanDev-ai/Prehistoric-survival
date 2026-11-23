package com.prehistoricsurvival.client.renderer;

import com.prehistoricsurvival.client.model.TheropodModel;
import com.prehistoricsurvival.client.model.TheropodSpecs;
import com.prehistoricsurvival.entity.VelociraptorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class VelociraptorRenderer extends DinoRenderer<VelociraptorEntity, TheropodModel<VelociraptorEntity>> {
    public VelociraptorRenderer(EntityRendererProvider.Context context) {
        super(context, new TheropodModel<>(context.bakeLayer(TheropodModel.RAPTOR_LAYER), TheropodSpecs.VELOCIRAPTOR),
                0.6f, "velociraptor", new String[] {"olive", "ember", "slate"});
    }
}
