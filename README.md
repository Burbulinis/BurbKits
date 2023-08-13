# BurbKits

**BurbKits** is a simple Minecraft plugin to create cool kits.
For any issues or suggestions, please make sure to visit [right here](https://github.com/Burbulinis/BurbKits/issues)
### 🎋 CONFIG 🎋
Super easy way to customize messages and the manage kits permission


## Downloads ✔
  You can find every version of this plugin [here](https://github.com/Burbulinis/BurbKits/releases), **this plugin only works with 1.13+ minecraft versions**

## Commands 📢
  - `/kits claim <kitname>`
  - `/kits info <kitname>`
  - `/kits manage create <kitname>` may throw a `RuntimeException` if the kit already exists
  - `/kits manage edit <kit> cooldown|permission|cooldownBypass set|remove|delete <value>`
  - `/kits manage edit <kit> override|delete`

## IMPORTANT ❗❗
  **Do not** change any of the values in the files `kits.yml`, or `cooldowns.yml`, unless you **know what you are doing**


# 🌴 Skript Support 🌴

#### **Skript** support is finally here! 🎉🥳

To view all of the syntax, you can visit [this place](https://github.com/Burbulinis/BurbKits/tree/master/src/main/java/me/burb/burbkits/skript/elements)

### How to create a simple kit with Skript? 🎇✨

Creating a kit is by far super simple, you can use this [Create Kit EffectSection](https://github.com/Burbulinis/BurbKits/tree/master/src/main/java/me/burb/burbkits/skript/elements/sections/EffSecCreateKit.java)

Here I will be sharing some code examples :)

```applescript
set {_items::*} to items in player's inventory

create a new kit named "myKit" with the items stored in {_items::*}:
    set kit cooldown of kit to 10 hours
    set kit permission of kit to "myKit"
    set cooldown bypass permission of kit to "cooldownBypass"    
    broadcast "The kit %kit% has just been created!"
    # kit is now made!
make player claim the last created kit and storing the success in {_success} 
# This makes the player claim the kit, but it will store the success which depends if they can claim the kit or no
broadcast {_success} # False or true depending on that ^ :)
```
Now you may be thinking.. is that all? Oh no, no.. that's not all. This is just a portion

Anyways.. lets move on :)

Let's take a look at many more snippets and see what they can do...

```applescript
on kit claim attempt:
    if attempt was successful: # Checks if they successfully claimed the kit
        send "omg you're so cool, you claimed the kit!"
        reset the kit cooldown for player from the kit named "abc" # Resets the cooldown of the player
```

**NOTE:** The kit claim attempt event is used to keep track when they **try** to claim the kit, they may not succeed, but the event is called. Use the kit claim event to kow when they claim a kit successfully.. but you cannot cancel that event. So you will have to use the kit claim attempt event :)

Anyways, this is a cool command to change the name of a kit ✨
```applescript
command /changename <string> <string>:
    trigger:
        set kit name of kit named arg-1 to arg-2 # Changes the kit arg-1's name to arg-2, pretty cool, eh?
        send "&aYou just changed the name of the kit %arg-1% to %arg-2%!" to player
```

Okay.. I can't think of much else.. OH! The very cool effects:
Here is what each effect do:
```applescript
make player claim kit named "myKit" and store the success in {_abc} # We already went over this.. but why not again? So this basically makes the player claim the kit, and you can store the success in a variable, though that's optional

give kit kit named "myKit" without dropping the rest of the items # You can use this to forcefully give a kit to a player.. ignore the 'kit kit', that's just for Skript to know that it's my syntax :( You can optionally not drop the leftover items that should be on the ground :)

open the kit-inventory of kit named "myKit" to player # This opens up the kit inventory of the kit, basically where all the items are

set {_items::*} to items in player's inventory
override the kit-items of kit named "myKit" with {_items::*} # Used to basically set the items of a kit to what you want 
```

Now there are a few conditions too, which are useful.. here is a few of them:

```applescript
if player has the cooldown of kit named "myKit":
    send "LOL! Noob, you have the cooldown.. HAHAHAHAH!!" to player 
# a good way to make fun of someone for having a cooldown ^

if player has the permission of kit named "myKit":
    send "woaaah pro :)"
# Self-explanatory, used to check if they have the permission
```

For any issues or suggestions, please check out the issues page :) Thank you so much for reading this.. now bye! 🙋‍♀️🙋‍♀️
