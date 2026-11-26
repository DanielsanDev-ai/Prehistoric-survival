package com.prehistoricsurvival.block;

import net.minecraft.world.level.block.Block;

/**
 * Nido. Es un bloque completo con una cavidad visual en la parte superior donde se hunde el huevo.
 * Hay tres tipos (paja, barro y monticulo) y cada especie prefiere uno; el tipo preferido acelera mas la incubacion.
 */
public class NestBlock extends Block {
    public NestBlock(Properties properties) {
        super(properties);
    }
}
