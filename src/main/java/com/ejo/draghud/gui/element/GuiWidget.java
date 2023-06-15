package com.ejo.draghud.gui.element;

import com.ejo.draghud.event.EventRegistry;
import com.ejo.draghud.gui.element.elements.window.GuiWindow;
import com.ejo.draghud.util.DrawUtil;
import com.ejo.draghud.util.SettingWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import org.util.glowlib.event.EventAction;
import org.util.glowlib.math.Vector;
import org.util.glowlib.misc.ColorE;
import org.util.glowlib.misc.Container;
import org.util.glowlib.util.NumberUtil;

public abstract class GuiWidget {

    private final Screen screen;

    protected Container<Vector> pos;
    protected Container<Vector> size;

    private boolean mouseOver;

    private boolean shouldDraw;

    private float hoverFade = 0;

    private final EventAction ANIMATION_HOVER_FADE = new EventAction(EventRegistry.EVENT_CALCULATE_ANIMATION,() -> {
        hoverFade = getNextFade(isMouseOver(), hoverFade, 2, 50, 1f);
    });


    public GuiWidget(Screen screen, Vector pos, Vector size, boolean shouldDraw) {
        this.screen = screen;
        this.pos = new Container<>(pos);
        this.size = new Container<>(size);
        this.shouldDraw = shouldDraw;
        this.ANIMATION_HOVER_FADE.subscribe();
    }


    public void draw(PoseStack stack, Vector mousePos) {
        mouseOver = updateMouseOver(mousePos);
        drawWidget(stack, mousePos);
        DrawUtil.drawRectangle(stack, getPos(), getSize(), new ColorE(255, 255, 255, (int) hoverFade));
        if (isMouseOver() && !(this instanceof GuiWindow)) {
            try {
                int xOffset = 6;
                int yOffset = -8;
                DrawUtil.drawRectangle(stack, mousePos.getAdded(xOffset,yOffset), new Vector(DrawUtil.getTextWidth(getSetting().getDescription()) + 4, 13), new ColorE(50, 50, 50, 150));
                DrawUtil.drawText(stack,getSetting().getDescription(),mousePos.getAdded(2 + xOffset,2 + yOffset),ColorE.WHITE);
            } catch (Exception e) {
                //
            }
        }
    }

    protected abstract void drawWidget(PoseStack stack, Vector mousePos);

    public abstract void mousePressed(int button, int state, Vector mousePos);

    public abstract void keyPressed(int key, int scancode, int modifiers);


    public float getNextFade(boolean condition, float fade, int min, int max, float speed) {
        if (condition) {
            if (fade < max) fade += speed;
        } else {
            if (fade > min) fade -= speed;
        }
        fade = NumberUtil.getBoundValue(fade,0,255).floatValue();
        return fade;
    }


    protected boolean updateMouseOver(Vector mousePos) {
        boolean mouseOverX = mousePos.getX() >= getPos().getX() && mousePos.getX() <= getPos().getX() + getSize().getX();
        boolean mouseOverY = mousePos.getY() >= getPos().getY() && mousePos.getY() <= getPos().getY() + getSize().getY();
        return mouseOver = mouseOverX && mouseOverY;
    }

    public boolean isDrawn() {
        return shouldDraw;
    }

    public void setPos(Vector vector) {
        pos.set(vector);
    }

    public void setSize(Vector vector) {
        this.size.set(vector);
    }


    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setDrawn(boolean drawn) {
        shouldDraw = drawn;
    }

    public Vector getPos() {
        return pos.get();
    }

    public Vector getSize() {
        return size.get();
    }

    public Screen getScreen() {
        return screen;
    }

    public SettingWidget getSetting() {
        return null;
    }


}
