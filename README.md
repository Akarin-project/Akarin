# <img src="https://i.loli.net/2018/05/17/5afd869c443ef.png" alt="Akarin Face" align="right">Akarin
[![Powered by](https://img.shields.io/badge/Powered_by-Akarin_project-ee6aa7.svg?style=flat)](https://akarin.io)
[![Chat](https://img.shields.io/badge/chat-on%20discord-7289da.svg)](https://discord.gg/fw2pJAj)
[![bStats](https://img.shields.io/badge/bStats-Torch-0099ff.svg?style=flat)](https://bstats.org/plugin/bukkit/Torch)
[![Circle CI](https://circleci.com/gh/Akarin-project/Akarin/tree/master.svg?style=svg)](https://circleci.com/gh/Akarin-project/Akarin/tree/ver/1.12.2)

Akarin is currently **under heavy development** and contributions are welcome!

Introduction
---
> Akarin is a powerful server software from the 'new dimension', formerly known as Torch.

As a [Paper](https://github.com/PaperMC/Paper) fork, it should support almost all plugins that work on [Spigot](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse).

Our project has a few key goals:

* **Open Access** - Make more game mechanics configurable.
* **Bedrock** - Make the server more safe and stable. 
* **Fast** - Simplify the logic and implement multi-threaded computing.

*Issues and Pull Requests will be labeled accordingly*

Get Akarin
---
### Download
#### Recommended
+ ~~[**Jenkins**](http://ci.ilummc.com/job/Akarin/)~~ - Kudos to [Izzel_Aliz](https://github.com/IzzelAliz)
+ [**Circle CI**](https://circleci.com/gh/Akarin-project/Akarin/tree/ver/1.12.2) - Checkout the 'Artifacts' tab of the latest build

*Open an [Issue](https://github.com/Akarin-project/Akarin/issues) or a [Pull Request](https://github.com/Akarin-project/Akarin/pulls) if you want to add your website here*

### Build
#### Requirements
* Java (JDK) 8 or above
* Maven

#### Compile
```sh
./scripts/inst.sh --setup --fast
```

**Notes**
* You must use `--setup` at least once to deploy necessary dependencies otherwise some imports cannot be organized.
* For non-modified projects, it is recommended to add the `--fast` option to skip any tests.
* If your machine has insufficient memory, you may want to add the `--remote` option to avoid decompiling locally.

Demo Servers
---
* `demo.akarin.io` (official)
* `omc.hk` (auth required)

*Open an [Issue](https://github.com/Akarin-project/Akarin/issues) or a [Pull Request](https://github.com/Akarin-project/Akarin/pulls) if you want to add your website here*

Contributing
---
* Akarin uses [Mixin](https://github.com/SpongePowered/Mixin) to modify the code. You can checkout the `sources` folder to see more. 
* Add your name to the [LICENSE](https://github.com/Akarin-project/Akarin/blob/master/LICENSE.md) if you want to publish your code under the [MIT License](https://github.com/Akarin-project/Akarin/blob/master/licenses/MIT.md).
* If you want to join the [Akarin-project](https://github.com/Akarin-project) team, you can [send](mailto://kira@kira.moe) us an email with your experience and necessary information.

![Akarin project](https://i.loli.net/2018/05/13/5af7fbbfbcddf.png)
