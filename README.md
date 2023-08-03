# BurbKits

**BurbKits** is a simple Minecraft plugin to create cool kits.
For any issues or suggestions, please make sure to visit [right here](https://github.com/Burbulinis/BurbKits/issues)

## Downloads ✔
  You can find every version of this plugin [here](https://github.com/Burbulinis/BurbKits/releases), **this plugin only works with 1.13+ minecraft versions**

## Commands 📢
  - `/kits create <kitname>` may throw a `RuntimeException` if the kit already exists
  - `/kits delete <kitname>`
  - `/kits override <kitname>`
  - `/kits claim <kitname>`
  - `/kits info <kitname>`
  - `/kits setPermission <kitname> <permission>`
  - `/kits setCooldown <kitname> <cooldown>` may throw an `IllegalArgumentException` if the cooldown was formatted incorrectly
  - `/kits resetCooldown <kitname> <offlineplayer>` 
  - `/kits setCooldownBypass <kitname> <permission>`
  - `/kits removeCooldownBypass <kitname>`
  - `/kits removePermission <kitname>`

## IMPORTANT ❗❗
  **Do not** change any of the values in the files `kits.yml`, or `cooldowns.yml`, unless you **know what you are doing**
  
  The permission to change, create, or delete kits is named `burbkits.managekits`
