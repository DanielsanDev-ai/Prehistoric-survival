package com.prehistoricsurvival.client;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.client.model.DodoModel;
import com.prehistoricsurvival.client.model.TheropodModel;
import com.prehistoricsurvival.client.model.TheropodSpecs;
import com.prehistoricsurvival.client.renderer.DodoRenderer;
import com.prehistoricsurvival.client.renderer.TyrannosaurusRenderer;
import com.prehistoricsurvival.client.renderer.VelociraptorRenderer;
import com.prehistoricsurvival.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PrehistoricSurvival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DodoModel.LAYER, DodoModel::createBodyLayer);
        event.registerLayerDefinition(TheropodModel.RAPTOR_LAYER, () -> TheropodModel.createLayer(TheropodSpecs.VELOCIRAPTOR));
        event.registerLayerDefinition(TheropodModel.REX_LAYER, () -> TheropodModel.createLayer(TheropodSpecs.TREX));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DODO.get(), DodoRenderer::new);
        event.registerEntityRenderer(ModEntities.VELOCIRAPTOR.get(), VelociraptorRenderer::new);
        event.registerEntityRenderer(ModEntities.TYRANNOSAURUS.get(), TyrannosaurusRenderer::new);
    }

    private ClientEvents() {}
}
