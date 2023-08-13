package me.burb.burbkits.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.skript.utils.SkriptUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EffClaimKit extends Effect {

    static {
        Skript.registerEffect(EffClaimKit.class,
                "force %players% to claim [[the] kit] %kit% [(and store|storing) the success in %-objects%]",
                "make %players% claim [[the] kit] %kit% [(and store|storing) the success in %-objects%]"
        );
    }

    private Expression<Player> players;
    private Expression<Kit> kit;
    private boolean isLocal;
    private Expression<?> object;

    @Override
    protected void execute(@NotNull Event event) {
        Player[] player = this.players.getArray(event);
        Kit kit = this.kit.getSingle(event);
        if (kit != null && object != null) {
            for (Player p : player) {
                boolean success = kit.claimKit(p, false);
                if (object instanceof Variable && ((Variable<?>) object).isList()) {
                    Variable<?> var = (Variable<?>) object;
                    VariableString variableString = SkriptUtil.getVariableName(var);
                    String varName = variableString.toString(event);
                    String name = varName.substring(0, varName.length() - 3);
                    Variables.setVariable(name + "::" + p.getUniqueId(), success, event, isLocal);
                } else {
                    object.change(event, new Object[]{success}, ChangeMode.SET);
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "make player claim kit effect with expr player: " + players.toString(event, b) + " with object expression" + object;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        kit = (Expression<Kit>) exprs[1];
        if (exprs.length == 3) {
            if (exprs[2].acceptChange(ChangeMode.SET) != null) {
                object = exprs[2];
                if (exprs[2] instanceof Variable) {
                    Variable<?> var = (Variable<?>) exprs[2];
                    isLocal = var.isLocal();
                }
            } else {
                Skript.error("You can't store the success in " + exprs[2]);
                return false;
            }
        }
        return true;
    }
}
