package pl.touk.liero

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.random
import pl.touk.liero.ecs.Entity
import pl.touk.liero.ecs.body
import pl.touk.liero.entity.entity
import pl.touk.liero.script.Script
import pl.touk.liero.game.PlayerControl
import pl.touk.liero.script.LifeTimeScript
import pl.touk.liero.system.SoundSystem
import pl.touk.liero.utils.then

class PlayerScript(val ctx: Ctx, val control: PlayerControl) : Script {

    override fun update(me: Entity, timeStepSec: Float) {
        val b = me[body]
        control.fireJustPressed.then {
            val quack1Or2 = random.nextInt(2)
            when(quack1Or2) {
                0 -> ctx.sound.playSoundSample(SoundSystem.SoundSample.Quack1)
                1 -> ctx.sound.playSoundSample(SoundSystem.SoundSample.Quack2)
            }

            ctx.engine.entity {
                text("kwa", b.position, Color.WHITE, ctx.smallFont)
                script(LifeTimeScript(1f))
            }
        }
        control.left.then {
            b.setLinearVelocity(-ctx.params.playerSpeed, b.linearVelocity.y)
        }
        control.right.then {
            b.setLinearVelocity(ctx.params.playerSpeed, b.linearVelocity.y)
        }
    }
}