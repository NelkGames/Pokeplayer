package pokecube.pokeplayer.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import pokecube.pokeplayer.Pokeplayer;
import pokecube.pokeplayer.block.PokeTransformContainer;

public class ContainerInit {

	// Tile
    public static final RegistryObject<MenuType<PokeTransformContainer>> TRANSFORM_CONTAINER;
    
    static
    {
    	TRANSFORM_CONTAINER = Pokeplayer.CONTAINER.register("pokeplayer_transform",
    			() -> IForgeMenuType.create(PokeTransformContainer::new));
    }
    
    public static void init() {}
}
