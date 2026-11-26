package com.prehistoricsurvival.block;

import java.util.List;

import javax.annotation.Nullable;

import com.prehistoricsurvival.registry.ModBlockEntities;
import com.prehistoricsurvival.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Huevo de dinosaurio. Un unico bloque con tres propiedades visuales:
 * KIND (aspecto segun la especie), HATCH (etapa de eclosion: 0 intacto, 1 agrietado, 2 a punto de romper)
 * y NESTED (si descansa sobre un nido, el modelo se hunde en la cavidad del nido).
 * Los datos (especie, genetica, progreso) viven en el DinoEggBlockEntity y se conservan al recogerlo.
 */
public class DinoEggBlock extends BaseEntityBlock {
    public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);
    public static final IntegerProperty KIND = IntegerProperty.create("kind", 0, 3);
    public static final BooleanProperty NESTED = BooleanProperty.create("nested");

    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 13.0, 13.0);
    private static final VoxelShape SHAPE_NESTED = Block.box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0);

    /** Especie por defecto de cada aspecto (para huevos sin datos, p. ej. los de /setblock). */
    public static final String[] KIND_SPECIES = {
            "prehistoricsurvival:dodo", "prehistoricsurvival:velociraptor",
            "prehistoricsurvival:tyrannosaurus", "prehistoricsurvival:dilophosaurus"};
    public static final int[] KIND_INCUBATION = {9000, 12000, 18000, 12000};

    public DinoEggBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HATCH, 0).setValue(KIND, 0).setValue(NESTED, false));
    }

    /** 0 = dodo, 1 = velociraptor, 2 = tyrannosaurus, 3 = dilophosaurus. */
    public static int kindOf(String speciesId) {
        if (speciesId.endsWith("velociraptor")) return 1;
        if (speciesId.endsWith("tyrannosaurus")) return 2;
        if (speciesId.endsWith("dilophosaurus")) return 3;
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, KIND, NESTED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(NESTED) ? SHAPE_NESTED : SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DinoEggBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.DINO_EGG.get(), DinoEggBlockEntity::serverTick);
    }

    /** Al colocar un huevo desde el inventario sin datos, se inicializa con los de su especie. */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof DinoEggBlockEntity egg) {
            if (stack.getTagElement("BlockEntityTag") == null && stack.getItem() instanceof DinoEggItem item) {
                egg.setup(item.getSpeciesId(), item.getIncubationTicks(), 1, 0, level.random.nextBoolean(), false, null, null);
            }
            boolean nested = level.getBlockState(pos.below()).getBlock() instanceof NestBlock;
            level.setBlock(pos, state.setValue(KIND, kindOf(egg.getSpecies())).setValue(NESTED, nested), 3);
        }
    }

    /** El huevo recogido conserva especie, genetica y progreso de incubacion. */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        Item item = ModItems.DODO_EGG.get();
        ItemStack stack;
        if (be instanceof DinoEggBlockEntity egg) {
            item = ModItems.eggFor(egg.getSpecies());
            stack = new ItemStack(item);
            stack.addTagElement("BlockEntityTag", egg.saveWithoutMetadata());
        } else {
            stack = new ItemStack(item);
        }
        return List.of(stack);
    }
}
