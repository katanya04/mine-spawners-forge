package me.katanya04.minespawnersforge.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A square button with an image of a pickaxe, upon clicking toggles the blacklisted state of that pickaxe
 */
@OnlyIn(Dist.CLIENT)
public class PickaxeButton extends Button {
    public final Item pickaxe;
    public final Font font;
    public PickaxeButton(int x, int y, int size, Item pickaxe, Font font) {
        super(x, y, size, size, Component.empty(),
                self -> {
                    List<String> blacklistedPickaxes = (List<String>) Config.BLACKLISTED_PICKAXES.get();
                    if (blacklistedPickaxes.contains(pickaxe.toString())) {
                        blacklistedPickaxes.remove(pickaxe.toString());
                    } else {
                        blacklistedPickaxes.add(pickaxe.toString());
                    }
                    Config.BLACKLISTED_PICKAXES.set(blacklistedPickaxes);
                    Config.BLACKLISTED_PICKAXES.save();
                },
                supplier ->
                        MutableComponent.create(new PlainTextContents.LiteralContents(pickaxe.getName().getString()))
        );
        this.pickaxe = pickaxe;
        this.font = font;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        int itemX = this.getX() + (this.width - 16) / 2;
        int itemY = this.getY() + (this.height - 16) / 2;
        graphics.renderItem(this.pickaxe.getDefaultInstance(), itemX, itemY);
        if (this.isHovered())
            graphics.renderTooltip(font, this.pickaxe.getDefaultInstance(), mouseX, mouseY);
        if (Config.BLACKLISTED_PICKAXES.get().contains(pickaxe.toString()))
            graphics.drawCenteredString(font, "X", getX(), getY(), 16711680);
    }
}
