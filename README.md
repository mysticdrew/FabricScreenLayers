# FabricScreenLayers

FabricScreenLayers is modelled after Minecraft Forge's GuiScreenLayering logic using mixins.
Provides utility to layer minecraft gui screens on top of one another. 

### Add to project:
```
repositories {
    maven {
        url = "https://jm.gserv.me/repository/maven-public/"
    }
}
dependencies {
    modImplementation 'net.mysticdrew:fabric-screen-layers:1.0.1+1.19.3'
}
```


#### Downloads:
- [CurseForge][1]
- [Modrinth][2]

Examples Usages: 

To add a screen on top of another screen:
```java
ScreenLayerManager.pushLayer(new CustomScreen());
```

To remove a screen:
```java
ScreenLayerManager.popLayer();
```

To clear all screens:
```java
ScreenLayerManager.clearLayers();
```

When translating a screen or item on the screen for sizing. 
It is important to use `ScreenLayerManager.getFarPlane()`so it does not break mods using layers.
```java
RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 100.0F, ScreenLayerManager.getFarPlane());
RenderSystem.setProjectionMatrix(matrix4f);
PoseStack posestack = RenderSystem.getModelViewStack();
posestack.setIdentity();
posestack.translate(0.0D, 0.0D, 1000.0F - ScreenLayerManager.getFarPlane());
```

ExampleMod Testing
- Clone or download the repo.
- Import build.gradle
- Run `ExampleMod Client` run config. 
- Press N to add the first screen
- Continue pressing N to add more, or click the buttons.

[1]: https://www.curseforge.com/minecraft/mc-mods/fabric-screen-layers

[2]: https://modrinth.com/mod/fabric-screen-layers

