package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;

public abstract class KitEvent extends Event {
    private final Kit kit;
    protected KitEvent(Kit kit) {
        this.kit = kit;
    }
    public Kit getKit() {
        return kit;
    }
}