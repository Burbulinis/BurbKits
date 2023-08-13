package me.burb.burbkits.skript.elements.expressions.event;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprKit extends EventValueExpression<Kit> {

    static {
        Skript.registerExpression(ExprKit.class, Kit.class, ExpressionType.SIMPLE,
                "[event(-| )]kit",
                "[the] kit"
        );
    }

    public ExprKit() {
        super(Kit.class);
    }

    @Override
    public @NotNull String toString(Event e, boolean b) {
        return "event-kit";
    }

}
