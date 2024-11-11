# BlackWidow

BlackWidow is a security solution for Minecraft which aims to offer out-of-the-box defense against vulnerabilities which are commonly exploited on online servers.

## Features

- **Command Blocking**

  Featuring a comprehensive command blocker, supporting blacklist/whitelisting, regex, powerful custom command rule chains, colon syntax blocking (`/plugin:command`), and more.

  The default configuration blocks common commands used by players to check installed plugins and versions.

- **Secure Defaults**

  The default configuration of BlackWidow provides immediate drag-and-drop security improvements for most servers.

- **Unit Testing**

  Standard component logic, such as the Command Blocking logic, is unit-tested to ensure it is working correctly before any version can be shipped out.

- **Simple & Reliable**

  BlackWidow is built to be robust and lightweight, and doesn't mash half-baked features together to seem appealing (..only to break next update).

## Get Started

### Compatibility

Firstly, make sure your software setup is compatible with BlackWidow.

Please reference the [Requirements](https://github.com/ArcanePlugins/BlackWidow/wiki/Requirements) page for the most up-to-date and descriptive information on the requirements of running BlackWidow.

The best-case scenario to run BlackWidow is:

- [x] **Minecraft 1.21.3** or newer
- [x] **PaperMC** or **SpigotMC** server software
- [x] **Java 21** or newer

Please be advised:

> We are considering adding future support for Velocity, BungeeCord, and Minestom. Let us know if you're interested!

> Derivatives of Spigot/Paper, such as Purpur or Pufferfish may work fine, but we don't support these setups. That being said, still give it a shot and see if everything works. :)

> Please note that we are not interested in backporting BlackWidow to older versions of Minecraft/Java/etc. Please update your software, or feel free to fork BlackWidow and backport it.

> We highly recommend **against** using any server software like Magma, Mohist, and Arclight which are basically try to make Forge mods work with Bukkit plugins. Bukkit was *never* designed to work with Forge/Fabric/etc mods. Trying to mix the two often causes lots of unusual issues which burden server owners and plugin maintainers. Thus, we don't provide any support whatsoever for software of this kind.
