package net.thevpc.gaming.helloworld;

import net.thevpc.gaming.atom.annotations.AtomSprite;
import net.thevpc.gaming.atom.annotations.Inject;
import net.thevpc.gaming.atom.annotations.OnInit;
import net.thevpc.gaming.atom.engine.SceneEngine;
import net.thevpc.gaming.atom.engine.collisiontasks.BounceSpriteCollisionTask;
import net.thevpc.gaming.atom.engine.collisiontasks.StopSpriteCollisionTask;
import net.thevpc.gaming.atom.engine.maintasks.MoveSpriteMainTask;
import net.thevpc.gaming.atom.model.Sprite;

/**
 * Created by vpc on 9/23/16.
 */
@AtomSprite(
        name = "Ball2",
        kind = "Ball2",
        sceneEngine = "hello",
        x=4,
        y=4,
        direction = (Math.PI*3/4),
        height = 2,
        width = 2,
        speed = 0.2,
        mainTask = MoveSpriteMainTask.class,
        collisionTask = StopSpriteCollisionTask.class
//        collisionTask = BounceSpriteCollisionTask.class

)

public class Ball2 {
    @Inject
    Sprite sprite;
    @Inject
    SceneEngine sceneEngine;

    @OnInit
    private void init(){
        sprite.setLocation(10,0);
    }
}
