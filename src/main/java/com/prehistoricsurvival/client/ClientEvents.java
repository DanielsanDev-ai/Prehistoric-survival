package com.prehistoricsurvival.client;

import com.prehistoricsurvival.PrehistoricSurvival;
import com.prehistoricsurvival.client.model.DodoModel;
import com.prehistoricsurvival.client.model.TheropodModel;
import com.prehistoricsurvival.client.model.TheropodSpecs;
import com.prehistoricsurvival.client.renderer.DilophosaurusRenderer;
import com.prehistoricsurvival.client.renderer.DodoRenderer;
import com.prehistoricsurvival.client.renderer.TyrannosaurusRenderer;
import com.prehistoricsurvival.client.renderer.VelociraptorRenderer;
import com.prehistoricsurvival.registry.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
        event.registerLayerDefinition(TheropodModel.DILO_LAYER, () -> TheropodModel.createLayer(TheropodSpecs.DILOPHOSAURUS));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DODO.get(), DodoRenderer::new);
        event.registerEntityRenderer(ModEntities.VELOCIRAPTOR.get(), VelociraptorRenderer::new);
        event.registerEntityRenderer(ModEntities.TYRANNOSAURUS.get(), TyrannosaurusRenderer::new);
        event.registerEntityRenderer(ModEntities.DILOPHOSAURUS.get(), DilophosaurusRenderer::new);
        event.registerEntityRenderer(ModEntities.VENOM_SPIT.get(), ThrownItemRenderer::new);
    }

    private ClientEvents() {}
}
