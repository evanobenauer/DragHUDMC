package com.ejo.draghud.gui.element.elements;

import com.ejo.draghud.gui.element.GuiWidget;
import com.ejo.draghud.util.DrawUtil;
import com.ejo.draghud.util.Key;
import com.ejo.draghud.util.SettingWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import org.util.glowlib.math.MathE;
import org.util.glowlib.math.Vector;
import org.util.glowlib.misc.ColorE;
import org.util.glowlib.setting.SettingUI;
import org.util.glowlib.util.NumberUtil;

public class GuiSlider<T extends Number> extends GuiWidget {

    private final SettingUI<T> setting;
    private final String title;

    private boolean sliding;

    private ColorE color;

    private GuiSlider(Screen screen, String title, SettingUI<T> value, Vector pos, Vector size, ColorE color) {
        super(screen, pos, size, true);
        this.setting = value;
        this.color = color;
        this.title = title;
        this.sliding = false;
    }

    public GuiSlider(Screen screen, SettingUI<T> value, Vector pos, Vector size, ColorE color) {
        this(screen,value.getKey(),value,pos,size,color);
    }

    public GuiSlider(Screen screen, SettingWidget<T> value, Vector pos, Vector size, ColorE color) {
        this(screen,value.getName(),value,pos,size,color);
    }


    @Override
    protected void drawWidget(PoseStack stack, Vector mousePos) {
        //Updates the value of the setting based off of the current width of the slider
        if (isSliding()) {
            double settingRange = getSetting().getMax().doubleValue() - getSetting().getMin().doubleValue();

            double sliderWidth = mousePos.getX() - getPos().getX();
            double sliderPercent = NumberUtil.getBoundValue(sliderWidth, 0, getSize().getX()).doubleValue() / getSize().getX();

            double calculatedValue = getSetting().getMin().doubleValue() + (sliderPercent * settingRange);
            double val = MathE.roundDouble((((Math.round(calculatedValue / getSetting().getStep().doubleValue())) * getSetting().getStep().doubleValue())), 2); //Rounds the slider based off of the step val

            T value = getSetting().getType().equals("integer") ? (T) (Integer) (int) val : (T) (Double) val;
            getSetting().set(value);
        }

        //Draw the background
        DrawUtil.drawRectangle(stack, getPos(), getSize(), new ColorE(50, 50, 50, 200));

        //Draw the slider fill
        double sliderWidth = getSize().getX() * (getSetting().get().doubleValue() / (getSetting().getMax()).doubleValue());
        int border = 1;
        DrawUtil.drawRectangle(stack, getPos().getAdded(new Vector(border,border)), new Vector(sliderWidth -border, getSize().getY() - border*2), new ColorE(0, 125, 200, 150));

        //Draw the slider node
        int nodeWidth = (int) getSize().getX() / 20;
        double nodeX = sliderWidth - nodeWidth / 2f;
        if (nodeX + nodeWidth > getSize().getX()) nodeX = getSize().getX() - nodeWidth;
        if (nodeX < 0) nodeX = 0;
        DrawUtil.drawRectangle(stack,getPos().getAdded(new Vector(nodeX,0)),new Vector(nodeWidth,getSize().getY()),new ColorE(0,125,200,255));

        //Draw the slider text
        String title;
        if (getSetting().getType().equals("integer")) {
            title = getTitle() + ": " + getSetting().get().intValue();
        } else {
            title = getTitle() + ": " + String.format("%.2f", getSetting().get());
        }
        DrawUtil.drawText(stack, title, getPos().getAdded(new Vector(2, getSize().getY() / 2 - DrawUtil.getTextHeight() / 2)), ColorE.WHITE);
    }


    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (state == Key.ACTION_PRESS) {
            if (isMouseOver() && button == 0) {
                setSliding(true);
            }
        }

        if (state == Key.ACTION_RELEASE) {
            if (button == 0) {
                if (isSliding()) setSliding(false);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }


    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }

    public void setColor(ColorE color) {
        this.color = color;
    }


    public String getTitle() {
        return title;
    }

    public boolean isSliding() {
        return sliding;
    }

    public ColorE getColor() {
        return color;
    }


    public SettingUI<T> getSetting() {
        return setting;
    }
}
