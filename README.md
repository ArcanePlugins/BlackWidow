<div align="center">

# (Work in Progress!)

</div>

# BlackWidow

BlackWidow is a security solution for Minecraft which aims to offer out-of-the-box defense against vulnerabilities which are commonly exploited on online servers.

## Components

- **Command Blocking**

  Featuring a comprehensive command blocker, supporting blacklist/whitelisting, regex, powerful custom command rule chains, colon syntax blocking (`/plugin:command`), and more.

  The default configuration blocks common commands used by players to check installed plugins and versions.

## Other Features

- **Secure defaults**: The default configuration of BlackWidow will immediately provide drag-and-drop security improvements to your server.
- **Unit testing**: We try to test standard component logic, such as the Command Blocking logic, to ensure it is working correctly before any version can be shipped out.
- **Does what it says on the tin**: BlackWidow is built to be robust and lightweight, and doesn't try to mash half-baked features in to seem appealing (only to break between versions).

## Installation

### Compatibility

Firstly, make sure your software setup is compatible with BlackWidow.

- [x] **Java 21** (or newer - better!)
- [x] **SpigotMC** or **PaperMC** server software.

> We are considering adding future support for Velocity, BungeeCord, and Minestom. Let us know if you're interested!

> Derivatives of Spigot/Paper, such as Purpur or Pufferfish may work fine, but we don't support these setups. That being said, still give it a shot and see if everything works. :)

> Please note that we are not interested in backporting BlackWidow to older versions of Minecraft/Java/etc. Please update your software, or feel free to fork BlackWidow and backport it.

> We highly recommend **against** using any server software like Magma, Mohist, and Arclight which are basically try to make Forge mods work with Bukkit plugins. Bukkit was *never* designed to work with Forge/Fabric/etc mods. Trying to mix the two often causes lots of unusual issues which burden server owners and plugin maintainers. Thus, we don't provide any support whatsoever for software of this kind.

