package vapourdrive.agricultural_enhancements.content.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BaseInfoItemBlock extends BlockItem {

    protected final String info;
    protected ForgeConfigSpec.IntValue arg;

    public BaseInfoItemBlock(Block pBlock, Properties pProperties, String inInfo) {
        super(pBlock, pProperties);
        info = "agriculturalenhancements."+inInfo+".info";
    }

    public BaseInfoItemBlock(Block pBlock, Properties pProperties, String inInfo, ForgeConfigSpec.IntValue argIn) {
        super(pBlock, pProperties);
        info = "agriculturalenhancements."+inInfo+".info";
        arg = argIn;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if(arg != null){
            list.add(Component.translatable(info, arg.get()).withStyle(ChatFormatting.GRAY));
        } else {
            list.add(Component.translatable(info).withStyle(ChatFormatting.GRAY));
        }
    }
}

