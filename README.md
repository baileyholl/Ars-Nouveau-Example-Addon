# Ars-Nouveau-Example-Addon

An example repository for making an addon for Ars Nouveau! See the wiki for more info.

**Getting Started**

* Fork this repo for a ready to build work space.
* Join the Ars Nouveau discord to ask questions, get help, or get ideas.

Some useful links for beginners:
* https://mcforge.readthedocs.io/en/latest/
* https://forge.gemwire.uk/wiki/
* https://wiki.mcjty.eu/modding/index.php?title=Main_Page
* https://github.com/baileyholl/Ars-Nouveau/tree/1.18.x/src/main/java/com/hollingsworth/arsnouveau/api (Full API documentation)
* https://github.com/bernie-g/geckolib/wiki (Animation library used by Ars Nouveau, optional)

**Test in Prism Launcher**

Build, replace only this mod's jar in the local `Ars Draconis` instance, and
launch it with:

```bash
bash gradlew runPrismClient
```

The task removes matching `ars_trinkets-*.jar` files from the instance's
`minecraft/mods` directory before copying the new jar. Override the defaults
with `PRISM_INSTANCE_DIR`, `PRISM_INSTANCE_NAME`, or `PRISM_LAUNCHER` when
needed.

The shared IntelliJ run configuration `Ars Draconis Prism` runs this same
Gradle task. Select it in the Run Configuration dropdown and press Run; Prism
still performs the actual Minecraft launch using the instance's configured
Java, JVM arguments, memory, mods, and loader flags.
