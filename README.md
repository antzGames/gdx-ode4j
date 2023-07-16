# libGDX Open Dynamics Engine (ODE4J) physics engine LIBRARY

This is a ODE4J library compatible with all libGDX backends, including GWT.  It is based on
version 0.4.1 of [Open Dynamics Engine for Java](https://github.com/tzaeschke/ode4j).

## How to use in your project

You can use the `master-SNAPSHOT` tag as I do not plan to release different versions, only bug fixes. 

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

See the samples project for examples.

## Math classes

Ode4j has its own math classes similar to libGDX's Vector3, Matrix3, Matrix4, and Quaternion.

I added a math utility class called [Ode2GDXMathUtils](https://github.com/antzGames/gdx-ode4j/blob/master/core/src/main/java/com/antz/ode4libGDX/util/Ode2GdxMathUtils.java).  Use the following methods to create the libGDX Quaternion from ode4j's QuanternionC or DMatrix3C:

```java
    Quaternion q1 = Ode2GdxMathUtils.getGdxQuaternion(odeQuaternion);  
    Quaternion q2 = Ode2GdxMathUtils.getGdxQuaternion(odeMat3);
```

In addition ode4j uses double and not float like most of libGDX's math classes.

## Where to get ODE/ode4j documentation and help

ODE official manual: http://ode.org/wiki/index.php/Manual

By far the most useful part is the [HOWTO](http://ode.org/wiki/index.php/HOWTO) section

ode4j discord channel : https://discord.gg/UFXJcXv2P8 ode4j/Java

ode4j contains also some features that are not present in ODE, such as a ragdoll and heightfields with holes. See ode4j's [Wiki](https://github.com/tzaeschke/ode4j/wiki/Functionality-beyond-ODE).

The [ODE forum](https://groups.google.com/forum/#!forum/ode-users) is useful for questions around physics and general API usage.

The [ode4j forum](https://groups.google.com/forum/?hl=en#!forum/ode4j) is for problems and functionality specific to ode4j/Java.

There is also the [old website](https://tzaeschke.github.io/ode4j-old/), including some [screenshots](https://tzaeschke.github.io/ode4j-old/ode4j-features.html).

Here is a [Youtube video](https://www.youtube.com/watch?v=ENlpu_Jjp3Q) of a list of games that used ODE from 2002-2015.  You will be suprised how many of your favorite games used this physcis libary.

