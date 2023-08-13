package me.burb.burbkits.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class EffGiveKit extends Effect {

    static {
        Skript.registerEffect(EffGiveKit.class,
                "give [the] kit %kit% to %players% [1¦without dropping [[the] rest [of [the]]] items]",
                "give %players% [the] kit %kit% [1¦without dropping [[the] rest [of [the]]] items]"
        );
    }

    private boolean dropItems;
    private Expression<Kit> kit;
    private Expression<Player> players;

    @Override
    protected void execute(@NotNull Event event) {

        Player[] players = this.players.getArray(event);
        Kit kit = this.kit.getSingle(event);
        TreeMap<Integer, ItemStack> items = new TreeMap<>();

        if (kit == null) return;

        kit.getItems().forEach((key, value) -> items.put(key, value.clone()));

        for (Entry<Integer, ItemStack> entry : items.entrySet()) {

            Integer key = entry.getKey();
            ItemStack value = entry.getValue();

            for (Player player : players) {
                PlayerInventory inv = player.getInventory();
                if (inv.getItem(key) == null) {
                    inv.setItem(key, value);
                } else {
                    HashMap<Integer, ItemStack> leftOvers = inv.addItem(value);
                    if (!leftOvers.isEmpty() && dropItems) {
                        player.getWorld().dropItem(player.getLocation(), leftOvers.get(0));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "give players " + players.toString(event, b) + " the kit " + kit.toString(event, b);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, ParseResult parseResult) {
        dropItems = !(parseResult.mark == 1);
        players = (Expression<Player>) exprs[0];
        kit = (Expression<Kit>) exprs[1];
        return true;
    }
}
