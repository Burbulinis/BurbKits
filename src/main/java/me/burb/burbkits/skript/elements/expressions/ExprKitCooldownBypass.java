package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprKitCooldownBypass extends SimplePropertyExpression<Kit, String> {

    static {
        register(ExprKitCooldownBypass.class, String.class, "cooldown bypass permission", "kit");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "cooldown bypass permission";
    }

    @Override
    public @Nullable String convert(Kit kit) {
        return kit.getCooldownBypassPermission();
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, @NotNull ChangeMode mode) {

        Kit kit = getExpr().getSingle(event);
        String cooldownBypass = null;

        if (kit == null) return;

        if (delta != null) {
            cooldownBypass = String.valueOf(delta[0]);
        }

        switch (mode) {
            case ADD:
            case DELETE:
                kit.removeCooldownBypass();
                break;
            case REMOVE:
            case REMOVE_ALL:
            case RESET:
            case SET:
                kit.setCooldownBypassPermission(cooldownBypass);
                break;
            default:
                assert false;
        }
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return (mode == ChangeMode.DELETE || mode == ChangeMode.SET) ? CollectionUtils.array(Timespan.class) :  null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
