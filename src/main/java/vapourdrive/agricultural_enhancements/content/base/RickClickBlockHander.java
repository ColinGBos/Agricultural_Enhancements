package vapourdrive.agricultural_enhancements.content.base;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static vapourdrive.agricultural_enhancements.content.base.FarmerWrench.wrench;

@Mod.EventBusSubscriber
public class RickClickBlockHander {

    @SubscribeEvent
    public static void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(wrench)) {
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
