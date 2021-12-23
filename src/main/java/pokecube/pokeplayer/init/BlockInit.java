package pokecube.pokeplayer.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import pokecube.core.PokecubeItems;
import pokecube.pokeplayer.Pokeplayer;
import pokecube.pokeplayer.block.TransformBlock;

public class BlockInit {

	// Blocks
    public static final RegistryObject<Block> TRANSFORM;
    
    static
    {
    	TRANSFORM = Pokeplayer.BLOCKS.register("pokeplayer_transform",
    			() -> new TransformBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.STONE).strength(0,5f)));
    }
    
    public static void init()
    {
        for (final RegistryObject<Block> reg : Pokeplayer.BLOCKS.getEntries())
            Pokeplayer.ITEMS.register(reg.getId().getPath(), () -> new BlockItem(reg.get(), new Item.Properties()
                    .tab(PokecubeItems.TAB_BLOCKS)));
    }
}
