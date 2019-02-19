package pl.touk.liero

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import pl.touk.liero.system.*
import pl.touk.liero.game.PlayerButtonControl
import pl.touk.liero.game.WorldEngine
import pl.touk.liero.system.MusicSystem
import pl.touk.liero.system.TextSystem
import pl.touk.liero.ecs.Actions
import pl.touk.liero.system.ActionsSystem
import pl.touk.liero.ecs.Engine
import pl.touk.liero.ecs.Entity
import pl.touk.liero.gdx.shorter
import pl.touk.liero.level.Level
import pl.touk.liero.level.LevelLoader
import pl.touk.liero.screen.UiEvent
import pl.touk.liero.script.CameraScript

open class Ctx(val prefs: GamePreferences) {

    // params
    val params = Params()

    // input
    val mux = InputMultiplexer()

    // cameras, viewports
    val hudCamera = OrthographicCamera()
    val worldCamera = OrthographicCamera()
    val viewport = ScreenViewport(hudCamera)

    // rendering
    val batch = SpriteBatch()

    // hud
    val stage = Stage(viewport, batch)
    val uiEvents = UiEventQueue<UiEvent>()

    // assets
    val assetManager = AssetManager()
    val font: BitmapFont
    val smallFont: BitmapFont
    val gameAtlas: TextureAtlas
    val menuAtlas: TextureAtlas
    val skin: Skin

    // music, sounds
    val music = MusicSystem(prefs.music)

    // app state
    var currentLevel = 1
    val levelLoader = LevelLoader()

    // game
    val worldEngine = WorldEngine()
    val engine: Engine<Entity>
        get() = worldEngine.engine
    val world: World
        get() = worldEngine.world
    val actions: Actions
        get() = worldEngine.actions

    lateinit var level: Level
    var cameraScript = CameraScript(worldCamera, 9f, 16f)
    val playerControl = PlayerButtonControl()

    // game state
    var playerLives = 3

    init {
        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))

        assetManager.load("font.ttf", BitmapFont::class.java,
                FreetypeFontLoader.FreeTypeFontLoaderParameter().also {
                    it.fontFileName = "fonts/century-gothic.ttf"
                    it.fontParameters.size = Gdx.graphics.shorter() / 10
                })
        assetManager.load("small-font.ttf", BitmapFont::class.java,
                FreetypeFontLoader.FreeTypeFontLoaderParameter().also {
                    it.fontFileName = "fonts/century-gothic.ttf"
                    it.fontParameters.size = Gdx.graphics.shorter() / 16
                })
        assetManager.load("game.atlas", TextureAtlas::class.java)
        assetManager.load("menu.atlas", TextureAtlas::class.java)

        assetManager.finishLoading()

        font = assetManager.get("font.ttf")
        smallFont = assetManager.get("small-font.ttf")
        gameAtlas = assetManager.get("game.atlas")
        menuAtlas = assetManager.get("menu.atlas")
        skin = createSkin(smallFont, font, gameAtlas, menuAtlas)

        engine.add(
                WorldSystem(world, worldEngine, GlobalParams.fixed_time_step),
                ScriptUpdateSystem(engine),
                ActionsSystem(worldEngine, actions),
                SpriteRenderSystem(engine, batch, worldCamera),
                TextSystem(engine, batch, worldCamera, hudCamera),
                ScriptBeforeDestroySystem(engine),
                ParentChildSystem(engine),
                BodyDisposeSystem(engine))
    }

    fun dispose() {
        batch.dispose()
        skin.dispose()
        stage.dispose()
        music.dispose()
        worldEngine.dispose()
    }

    fun clearWorld() {
        worldEngine.clear()
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        cameraScript.resize(width.toFloat(), height.toFloat())
    }
}