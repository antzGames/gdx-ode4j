# libGDX Open Dynamics Engine (ODE4J) - physics engine LIBRARY

This is a ODE4J library compatible with all libGDX backends, including GWT.  It is based on
version 0.4.2 of [Open Dynamics Engine for Java](https://github.com/tzaeschke/ode4j).

If you want to use ode4j only on libGDX Desktop/Android/iOS backends then I recommend you use [odej4](https://github.com/tzaeschke/ode4j) directly.  
However if you want cross platform support (i.e include GWT support) then you need to use this library.

Currently this is the only 3D physics engine option for GWT on libGDX.

## How to use in your project

Add the dependency in your core project:

```gradle
project(":core") {
    ...

    dependencies {
        ...
          api "com.github.antzGames:gdx-ode4j:master-SNAPSHOT"
    }
}
```

If you are targeting HTML (GWT) you will also need the following:

```gradle
project(":html") {
    ...
    dependencies {
        ...
        implementation "com.github.antzGames:gdx-ode4j:master-SNAPSHOT:sources"
        implementation "com.github.tommyettinger:formic:0.1.4:sources"
    }
}
```

and lastly add this to your `GdxDefinition.gwt.xml` file:

```xml
<module>
    ...
    <inherits name="gdx_ode4j" />
    <inherits name="formic" />
    ...
</module>
```

## Runtime examples

Some examples can be found in my [gdx-ode4j-examples](https://github.com/antzGames/gdx-ode4j-examples) repository.

Or play with them on [itch.io](https://antzgames.itch.io/physics-in-a-browser).

https://github.com/antzGames/gdx-ode4j/assets/10563814/bc46a9a9-f2e8-414b-bfde-8be6ea54e46b

## Math classes

Ode4j has its own math classes similar to libGDX's Vector3, Matrix3, Matrix4, and Quaternion.

I added a math utility class called [Ode2GDXMathUtils](https://github.com/antzGames/gdx-ode4j/blob/master/src/main/java/com/github/antzGames/gdx/ode4j/Ode2GdxMathUtils.java).  Use the following methods to create the libGDX Quaternion from ode4j's QuanternionC or DMatrix3C:

```java
    Quaternion q1 = Ode2GdxMathUtils.getGdxQuaternion(odeQuaternion);  
    Quaternion q2 = Ode2GdxMathUtils.getGdxQuaternion(odeMat3);
```

In addition ode4j uses double and not float like most of libGDX's math classes.

## Limitations

### Fixed timesteps

ODE was made to work with fixed timesteps.  Do not pass `Gdx.graphics.getDeltaTime()` to `world.quickStep()`.

The following online [ODE HOWTO entry](https://ode.org/wiki/index.php/HOWTO_fixed_vs_variable_timestep) discusses how to incorporate this limitation into a game.

Using `vsync=true` in your game launch configuration helps, but some people might run at 60hz, while others might run at 50Hz, 75Hz, 144Hz.

Below is some example code that you can use to force physics to only update at a fixed interval (timestep).

```java
public static final float MIN_FRAME_LENGTH = 1f/60f;
        ...

@Override
public void render(float deltaTime){
        timeSinceLastRender+=deltaTime;

        // Only compute 60Hz for physics
        if (timeSinceLastRender >= MIN_FRAME_LENGTH) {
            odePhysicsSystem.update(timeSinceLastRender);   // This is my custom class
            timeSinceLastRender -= MIN_FRAME_LENGTH;

            if(timeSinceLastRender > MIN_FRAME_LENGTH)
                timeSinceLastRender = 0;
        }

        ...
}
```

### Performance

I have tried jBullet, PhysX and ODE physics engines with libGDX.  ODE is the slowest, and the reason being is that in ODE everything is using double precision.

### Known Issues

ODE4J 0.4.2 has some Triangle Mesh (TriMesh) collision detection issues.

The owner of ODE4J has fixed them in ODE4J 0.5.0.

I will have to update this library to use ODE4J 0.5.0 which is planned.

## Where to get ODE/ode4j documentation and help

ODE official manual: http://ode.org/wiki/index.php/Manual

By far the most useful part is the [HOWTO](http://ode.org/wiki/index.php/HOWTO) section

ode4j discord channel : https://discord.gg/UFXJcXv2P8 ode4j/Java

ode4j contains also some features that are not present in ODE, such as a ragdoll and heightfields with holes. See ode4j's [Wiki](https://github.com/tzaeschke/ode4j/wiki/Functionality-beyond-ODE).

The [ODE forum](https://groups.google.com/forum/#!forum/ode-users) is useful for questions around physics and general API usage.

The [ode4j forum](https://groups.google.com/forum/?hl=en#!forum/ode4j) is for problems and functionality specific to ode4j/Java.

There is also the [old website](https://tzaeschke.github.io/ode4j-old/), including some [screenshots](https://tzaeschke.github.io/ode4j-old/ode4j-features.html).

Here is a [Youtube video](https://www.youtube.com/watch?v=ENlpu_Jjp3Q) of a list of games that used ODE from 2002-2015.  You will be suprised how many of your favorite games used this physcis libary.

## ODE, ode4j and other Licenses

### Licensing & Copyright for ODE and ode4j

This library is under copyright by Tilmann Zäschke (Java port), Russell L. Smith (copyright holder of the original ODE code), Francisco Leon (copyright holder of the original GIMPACT code) and Daniel Fiser (copyright holder of the original libccd).

This library is free software; you can redistribute it and/or modify it under the terms of EITHER:
(1) The GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. The text of the GNU Lesser General Public License is included with this library in the file LICENSE.TXT.
(2) The BSD-style license that is included with this library in the files ODE-LICENSE-BSD.TXT and ODE4j-LICENSE-BSD.TXT.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files LICENSE.TXT, ODE-LICENSE-BSD.TXT, GIMPACT-LICENSE-BSD.TXT, GIMPACT-LICENSE-LGPL.TXT, ODE4J-LICENSE-BSD.TXT and LIBCCD_BSD-LICENSE for more details.

The LICENSE.TXT, ODE-LICENSE-BSD.TXT, GIMPACT-LICENSE-BSD.TXT, GIMPACT-LICENSE-LGPL.TXT, LIBCCD_BSD-LICENSE and ODE4J-LICENSE-BSD.TXT files are available in the source code.

## Legal

ode4j:
Copyright (c) 2009-2017 Tilmann Zäschke ode4j@gmx.de.
All rights reserved.

Like the original ODE, ode4j is licensed under LGPL v2.1 and BSD 3-clause. Choose whichever license suits your needs.

### ode4j contains Java ports of the following software

[ODE/OpenDE](http://www.ode.org/):
Copyright  (c) 2001,2002 Russell L. Smith
All rights reserved.

GIMPACT (part of ODE/OpenDE):
Copyright of GIMPACT (c) 2006 Francisco Leon. C.C. 80087371.
email: projectileman(AT)yahoo.com

[LIBCCD](https://github.com/danfis/libccd):
Copyright (c) 2010 Daniel Fiser <danfis(AT)danfis.cz>;
3-clause BSD License

[Turbulenz Engine](https://github.com/turbulenz/turbulenz_engine):
Copyright (c) 2009-2014 Turbulenz Limited; MIT License
