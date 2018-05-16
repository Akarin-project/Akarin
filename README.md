# Akarin
[![Minecraft](https://img.shields.io/badge/Minecraft-1.12-blue.svg?style=flat)](https://www.minecraft.net/)
[![bStats](https://img.shields.io/badge/bStats-Torch-blue.svg?style=flat)](https://bstats.org/plugin/bukkit/Torch)

Introduction
---
> Akarin is a powerful server software form the 'new dimension', formerly known as [Torch](https://github.com/GelandiAssociation/Torch).

As a [Paper](https://github.com/PaperMC/Paper) fork, it supports almost plugins that [Spigot](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse) can use.

It has a few key goals:
* Open Access - Make more game mechanism configurable.
* Bedrock - Safety and stable is important for a server. 
* Fast - Simplify the logic and import the multi-thread compute.

Akarin is **under heavy development** yet, contribution is welcome and run a test before putting into production.

Build/Compile
---
#### Requirements
* Java (JDK) 8 or above
* Maven

#### Build
```sh
./scripts/inst.sh --setup --fast
```
*For non-modification compile, add `--fast` option to skip the test is recommended.*

Contributing
---
* Feel free to open an issue if you have any problem with Akarin.
* Pull Request is welcomed, Akarin use [Mixin](https://github.com/SpongePowered/Mixin) to modify the code, you can checkout `sources` folder to see them. Moreover, add your name to the [LICENSE](.github/LICENSE.md) if you want to publish your code under the [MIT License](.github/licenses/MIT.md).
* If you want to join the Akarin-project team, you can send an email to `kira@kira.moe` with your experience and necessary information. Besides, welcome to join our [TIM Group](https://jq.qq.com/?_wv=1027&k=59q2kV4) to chat.

![Akarin project](https://i.loli.net/2018/05/13/5af7fbbfbcddf.png)