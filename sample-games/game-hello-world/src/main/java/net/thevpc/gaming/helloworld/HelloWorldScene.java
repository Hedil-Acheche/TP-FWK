package net.thevpc.gaming.helloworld;

import net.thevpc.gaming.atom.annotations.*;
import net.thevpc.gaming.atom.debug.AdjustViewController;
import net.thevpc.gaming.atom.engine.SceneEngine;
import net.thevpc.gaming.atom.engine.SpriteFilter;
import net.thevpc.gaming.atom.engine.maintasks.MoveToPointSpriteMainTask;
import net.thevpc.gaming.atom.model.ModelPoint;
import net.thevpc.gaming.atom.model.Orientation;
import net.thevpc.gaming.atom.model.Point;
import net.thevpc.gaming.atom.presentation.*;
import net.thevpc.gaming.atom.presentation.components.SLabel;
import net.thevpc.gaming.atom.presentation.components.SceneComponentState;
import net.thevpc.gaming.atom.presentation.layers.Layers;

import java.awt.*;
import java.time.format.TextStyle;

/**
 * Created by vpc on 9/23/16.
 */
@AtomScene(
        id = "hello",
        title = "Hello World",
        tileWidth = 80,
        tileHeight = 80
        
)
@AtomSceneEngine(
        id="hello",
        columns = 10,
        rows = 10,
        fps = 25
)
public class HelloWorldScene {

    @Inject
    private Scene scene;

    @Inject
    private SceneEngine sceneEngine;

    @OnSceneStarted
    private void init() {

        scene.addLayer(Layers.fillBoard(Color.BLUE));
        scene.addLayer(Layers.fillScreen(Color.RED));
        scene.addLayer(Layers.fillBoardImage("/itachi.jpeg"));
        scene.addLayer(Layers.debug());
        scene.addController(new SpriteController(SpriteFilter.byName("Ball1")).setArrowKeysLayout());
        scene.addController(new SpriteController2(SpriteFilter.byName("Ball2")).setESDFLayout());
       // scene.addController(new MouseMoveController(SpriteFilter.byName("Ball2")));
        scene.addController(new AdjustViewController());
        scene.addComponent(
                new SLabel("Click CTRL-D to switch debug mode, use Arrows to move the ball")
                .setLocation(Point.ratio(0.5f,0.5f))
        );
        scene.addComponent(new SLabel("Vies:3")
        .setLocation(Point.ratio(0f,0f))

        );

        scene.setSpriteView(SpriteFilter.byKind("Ball"), new ImageSpriteView("/ball.png", 8, 4));
        scene.setSpriteView(SpriteFilter.byKind("Ball2"), new ImageSpriteView("/256px-Mangekyou_Sharingan_Sasuke.svg.png", 1, 1));

    }
   // @OnNextFrame
   // public void aChaqueTic() {
       // SpriteModel ball = sceneEngine.findSpriteByName("Ball1");
       // if (ball != null) {
        //    int vies = ball.getLife();
        //    viesLabel.setText("Vies : " + vies);
       // }
  //  }

}
