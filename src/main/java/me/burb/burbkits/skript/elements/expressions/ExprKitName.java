package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprKitName extends SimplePropertyExpression<Kit, String> {

    static {
        register(ExprKitName.class, String.class, "kit name", "kit");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "kit name";
    }

    @Override
    public @Nullable String convert(Kit kit) {
        return kit.toString();
    }

    @Override
    public void change(@NotNull Event event, Object[] delta, @NotNull ChangeMode mode) {
        if (delta[0] != null) {

            Kit kit = getExpr().getSingle(event);
            String name = String.valueOf(delta[0]);

            if (kit == null) return;

            if (Kit.getKitFromName(name) != null) name = kit.toString();

            switch (mode) {
                case ADD:
                case DELETE:
                case REMOVE:
                case REMOVE_ALL:
                case RESET:
                case SET:
                    kit.setName(name);
                    break;
                default:
                    assert false;
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return (mode == ChangeMode.SET) ? CollectionUtils.array(String.class) :  null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
