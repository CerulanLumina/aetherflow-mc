package net.cerulan.luminality.container

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.util.Identifier

class WSpriteButton(vararg images: Identifier) : WWidget() {

    private val sprite = WSprite(images[0])
    var imageCount = images.size
        private set

    var currentImage = 0
        set(value) {
            field = if (value >= imageCount || value < 0) 0 else value
            sprite.setImage(sprites[field])
        }
    var onClickRun: (Int) -> Unit = { }
    var enabled = true
    var sprites = images
        set(value)  {
            field = value
            imageCount = value.size
        }


    override fun paintBackground(x: Int, y: Int, mouseX: Int, mouseY: Int) {
        val hovered =
            mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight()
        var state = 1
        if (!this.enabled) {
            state = 0
        } else if (hovered) {
            state = 2
        }

        val px = 1 / 256f
        val buttonLeft = 0 * px
        val buttonTop = (46 + state * 20) * px
        var halfWidth = getWidth() / 2
        if (halfWidth > 198) halfWidth = 198
        val buttonWidth = halfWidth * px
        val buttonHeight = 20 * px

        val buttonEndLeft = (200 - getWidth() / 2) * px

        ScreenDrawing.texturedRect(
            x,
            y,
            getWidth() / 2,
            getHeight(),
            AbstractButtonWidget.WIDGETS_LOCATION,
            0f,
            buttonTop,
            buttonLeft + buttonWidth,
            buttonTop + buttonHeight,
            -1
        )
        ScreenDrawing.texturedRect(
            x + getWidth() / 2,
            y,
            getWidth() / 2,
            getHeight(),
            AbstractButtonWidget.WIDGETS_LOCATION,
            buttonEndLeft,
            buttonTop,
            200.0f * px,
            buttonTop + buttonHeight,
            -1
        )

        sprite.paintBackground(x, y, mouseX, mouseY)
    }

    override fun setLocation(x: Int, y: Int) {
        super.setLocation(x, y)
        sprite.setLocation(x, y)
    }

    override fun setSize(x: Int, y: Int) {
        width = x
        height = y
        sprite.setSize(x, y)
    }

    override fun onClick(x: Int, y: Int, button: Int) {
        onClickRun(currentImage)
    }



}