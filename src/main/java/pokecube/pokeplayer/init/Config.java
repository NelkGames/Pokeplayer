package pokecube.pokeplayer.init;

import thut.core.common.config.Configure;
import pokecube.pokeplayer.Reference;
import thut.core.common.config.Config.ConfigData;

public class Config extends ConfigData {
	
	@Configure(category = "pokeplayer", comment = "The time for use Transform Block. 1 Tick = 20 seconds. Limit = 150 [Default: 50]")
    public int ticksBlockUse  = 50;
	
	public boolean loaded = false;
	
	public Config()
    {
        super(Reference.ID);
    }

	@Override
	public void onUpdated() {
		
		if (!this.loaded) return;
		if(this.ticksBlockUse > 150 || this.ticksBlockUse < 40) this.ticksBlockUse = 50;
	}
}
