package pokecube.pokeplayer.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import pokecube.pokeplayer.Pokeplayer;
import pokecube.pokeplayer.tileentity.TileEntityTransformer;

public class TileEntityInit {

	// Tile
    public static final RegistryObject<BlockEntityType<TileEntityTransformer>> TRANSFORM_TILE;
    
    static
    {
    	TRANSFORM_TILE = Pokeplayer.TILES.register("pokeplayer_transform", () -> BlockEntityType.Builder.of(
    			TileEntityTransformer::new, BlockInit.TRANSFORM.get()).build(null));
    }
    
    public static void init() {}
}
