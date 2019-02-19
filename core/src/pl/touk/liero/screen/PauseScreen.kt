package pl.touk.liero.screen

import pl.touk.liero.gdx.onClicked
import pl.touk.liero.Ctx
import ktx.scene2d.KTableWidget
import ktx.scene2d.button

class PauseScreen(ctx: Ctx) : GameOverlayScreen(ctx) {
    override fun getText() = "Pause"
    override fun createButtons(table: KTableWidget) {
        table.apply {
            button("back") { cell ->
                cell.size(ctx.params.buttonSize)
                onClicked {
                    ctx.uiEvents += UiEvent.Back
                }
            }
            button("play") { cell ->
                cell.size(ctx.params.buttonSize)
                onClicked {
                    ctx.uiEvents += UiEvent.Play
                }
            }
        }
    }

    override fun onEnter() {
        ctx.uiEvents += UiEvent.Play
    }
}