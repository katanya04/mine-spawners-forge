package me.katanya04.minespawnersforge.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

/**
 * The mod configuration screen, accesible from the "Mods" button in the main menu.
 * The configuration applies to clientside/single player... When using the mod serverside
 * only, just modify the value on the config toml file.
 */
@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {
    //private final Screen previousScreen;
    protected ConfigScreen() {
        super(MutableComponent.create(LiteralContents.EMPTY));
        //this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        StringWidget titleDrop = new StringWidget(MutableComponent.create(new LiteralContents("Drop chance: ")), this.minecraft.fontFilterFishy);
        titleDrop.setX(15);
        titleDrop.setY(15);
        addRenderableWidget(titleDrop);

        ForgeSlider sliderDrop = new ForgeSlider(titleDrop.getX() + titleDrop.getWidth() + 15, 15, 100, 20, MutableComponent.create(new LiteralContents("")),
                MutableComponent.create(new LiteralContents("% chance")), 0.0, 1.0, Config.DROP_CHANCE.getFloat(), 0.01, 0, true) {
            @Override
            public void setValue(double value) {
                value = Mth.clamp(value, 0.0, 1.0);
                super.setValue(value);
                Config.DROP_CHANCE.setValue(value);
            }

            @Override
            protected void applyValue() {
                super.applyValue();
                Config.DROP_CHANCE.setValue(value);
            }

            @Override
            protected @NotNull MutableComponent createNarrationMessage() {
                return MutableComponent.create(new LiteralContents("Drop chance: " + value * 100 + "% chance"));
            }

            @Override
            protected void updateMessage() {
                if (this.drawString) {
                    this.setMessage(Component.literal("").append(this.prefix).append(String.format("%.0f", this.value * 100)).append(this.suffix));
                } else {
                    this.setMessage(Component.empty());
                }

            }
        };
        addRenderableWidget(sliderDrop);

        titleDrop.setY(titleDrop.getY() + sliderDrop.getY() / 2);

        Button returnButton = Button.builder(MutableComponent.create(new LiteralContents("Return")), (button) -> this.onClose()).build();
        returnButton.setX(this.width / 2 - returnButton.getWidth() / 2);
        returnButton.setY(this.height - returnButton.getHeight() - 15);
        addRenderableWidget(returnButton);

        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int p_281550_, int p_282878_, float p_282465_) {
        this.renderDirtBackground(guiGraphics);
        super.render(guiGraphics, p_281550_, p_282878_, p_282465_);
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
