# <img src="https://i.loli.net/2018/05/17/5afd869c443ef.png" alt="Akarin Face" align="right">Akarin
[![Build Status](http://ci.pcd.ac.cn/job/Akarin/badge/icon)](http://ci.ilummc.com/job/Akarin/)
[![bStats](https://img.shields.io/badge/bStats-Torch-0099ff.svg?style=flat)](https://bstats.org/plugin/bukkit/Torch)
[![Powered by](https://img.shields.io/badge/Powered_by-Akarin_project-ee6aa7.svg?style=flat)](https://akarin.io)

Introduction
---
> Akarin is a powerful server software form the 'new dimension', formerly known as Torch.

As a [Paper](https://github.com/PaperMC/Paper) fork, it supports almost all plugins that [Spigot](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse) can use.

It has a few key goals:
* **Open Access** - Make more game mechanism configurable.
* **Bedrock** - Safety and stable is important for a server. 
* **Fast** - Simplify the logic and import the multi-thread compute.

Akarin is **under heavy development** yet, contribution is welcome and run a test before putting into production.

Get Akarin
---
### Download
#### Recommended Sites
+ [**Jenkins**](http://ci.ilummc.com/job/Akarin/) - *Kudos to [Izzel_Aliz](https://github.com/IzzelAliz)*
+ [![Circle CI](https://circleci.com/gh/Akarin-project/Akarin/tree/master.svg?style=svg) **Circle CI**](https://circleci.com/gh/Akarin-project/Akarin/tree/master) - Checkout the 'Artifacts' tab of the latest build  *Login required*

*Contact me via the email below or open an [Issue](https://github.com/Akarin-project/akarin/issues) if you want to add your website here*

### Build
#### Requirements
* Java (JDK) 8 or above
* Maven

#### Compile
```sh
./scripts/inst.sh --setup --fast
```
*For non-modification compile, add `--fast` option to skip the test is recommended.*
*Futhermore, if your machine have a insufficient memory, you may add `--remote` option to avoid decompile locally.*

Demonstration servers
---
+ **demo.akarin.io**

*Contact me via the email below or open an [Issue](https://github.com/Akarin-project/akarin/issues) if you want to add your server here*

Contributing
---
* Feel free to open an [Issue](https://github.com/Akarin-project/akarin/issues) if you have any problem with Akarin.
* [Pull Request](https://github.com/Akarin-project/akarin/pulls) is welcomed, Akarin use [Mixin](https://github.com/SpongePowered/Mixin) to modify the code, you can checkout `sources` folder to see them. Moreover, add your name to the [LICENSE](https://github.com/Akarin-project/Akarin/blob/master/LICENSE.md) if you want to publish your code under the [MIT License](https://github.com/Akarin-project/Akarin/blob/master/licenses/MIT.md).
* If you want to join the [Akarin-project](https://github.com/Akarin-project) team, you can send an email to `kira@kira.moe` with your experience and necessary information. Besides, welcome to join our [Discord](https://discord.gg/D3Rsukh) to chat.
* Note that you must `--setup` at least once to deploy necessary dependency otherwise some imports cannot be organized.

![Akarin project](https://i.loli.net/2018/05/13/5af7fbbfbcddf.png)