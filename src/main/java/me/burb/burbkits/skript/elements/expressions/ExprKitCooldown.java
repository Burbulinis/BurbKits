package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprKitCooldown extends SimplePropertyExpression<Kit, Timespan> {

    static {
        register(ExprKitCooldown.class, Timespan.class, "kit cooldown", "kit");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "kit cooldown";
    }

    @Override
    public @Nullable Timespan convert(Kit kit) {
        long cooldown = kit.getKitCooldown();
        return new Timespan(cooldown);
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, @NotNull ChangeMode mode) {

        Kit kit = getExpr().getSingle(event);
        Timespan cooldown = null;

        if (kit == null) return;

        if (delta != null) {
            cooldown = Timespan.parse(String.valueOf(delta[0]));
            if (cooldown == null) return;
        }

        switch (mode) {
            case ADD:
                kit.setKitCooldown(kit.getKitCooldown() + cooldown.getMilliSeconds());
                break;
            case DELETE:
                kit.removeCooldown();
                break;
            case REMOVE:
                kit.setKitCooldown(kit.getKitCooldown() - cooldown.getMilliSeconds());
                break;
            case REMOVE_ALL:
            case RESET:
            case SET:
                kit.setKitCooldown(cooldown.getMilliSeconds());
                break;
            default:
                assert false;
        }
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return (mode == ChangeMode.DELETE || mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) ? CollectionUtils.array(Timespan.class) :  null;
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }
}
