package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprKitPermission extends SimplePropertyExpression<Kit, String> {

    static {
        register(ExprKitPermission.class, String.class, "kit permission", "kit");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "kit permission";
    }

    @Override
    public @Nullable String convert(Kit kit) {
        return kit.getPermission();
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, @NotNull ChangeMode mode) {

        Kit kit = getExpr().getSingle(event);
        String permission = null;

        if (kit == null) return;

        if (delta != null) permission = String.valueOf(delta[0]);

        switch (mode) {
            case ADD:
            case DELETE:
                kit.removePermission();
                break;
            case REMOVE:
            case REMOVE_ALL:
            case RESET:
            case SET:
                kit.setPermission(permission);
                break;
            default:
                assert false;
        }
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return (mode == ChangeMode.DELETE || mode == ChangeMode.SET) ? CollectionUtils.array(String.class) :  null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
