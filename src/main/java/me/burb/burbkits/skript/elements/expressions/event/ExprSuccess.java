package me.burb.burbkits.skript.elements.expressions.event;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprSuccess extends EventValueExpression<Boolean> {

    static {
        Skript.registerExpression(ExprSuccess.class, Boolean.class, ExpressionType.SIMPLE,
                "[event(-| )]success",
                "[the] success"
        );
    }

    public ExprSuccess() {
        super(Boolean.class);
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "event-success";
    }
}
