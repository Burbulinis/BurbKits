package me.burb.burbkits.skript.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.StreamCorruptedException;
import java.util.Set;
import java.util.TreeMap;

import static me.burb.burbkits.BurbKits.kitsConfig;

public class Types {
    public Types() {}

    static {
        Converters.registerConverter(Kit.class, String.class, Kit::toString);
        Classes.registerClass(new ClassInfo<>(Kit.class, "kit")
                .user("kits?")
                .name("Kit")
                .description("Represents a Kit.")
                .examples("set {_kit} to a kit named \"my cool kit\"")
                .defaultExpression(new EventValueExpression<>(Kit.class))
                .since("2.0.0")
                .changer(new Changer<>() {

                    @Override
                    public Class<?>[] acceptChange(@NotNull ChangeMode changeMode) {
                        return changeMode == ChangeMode.DELETE ? CollectionUtils.array() : null;
                    }

                    @Override
                    public void change(Kit @NotNull [] kits, Object @NotNull [] objects, @NotNull ChangeMode changeMode) {
                        if (changeMode != ChangeMode.DELETE) return;
                        for (Kit kit : kits) {
                            kit.deleteKit();
                        }
                    }
                })
                .parser(new Parser<>() {

                    @Override
                    public @NotNull String toString(Kit kit, int i) {
                        return "kit '" + kit.toString() + "'";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Kit kit) {
                        return "kit '" + kit.toString() + "'";
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return true;
                    }

                    @Override
                    @Nullable
                    public Kit parse(@NotNull String input, @NotNull ParseContext context) {
                        return Kit.getKitFromName(input);
                    }
                })
                .serializer(new Serializer<>() {

                    @Override
                    public @NotNull Fields serialize(Kit kit) {
                        Fields f = new Fields();
                        f.putObject("name", kit.toString());
                        return f;
                    }

                    @Override
                    public Kit deserialize(@NotNull Fields fields) throws StreamCorruptedException {

                        String name = fields.getAndRemoveObject("name", String.class);
                        Kit kit = new Kit(name);
                        Set<String> slots = kitsConfig.getConfigurationSection("kits." + name + ".items").getKeys(false);
                        TreeMap<Integer, ItemStack> items = new TreeMap<>();

                        for (String slot : slots) {
                            ItemStack item = kitsConfig.getItemStack("kits." + name + ".items." + slot);
                            items.put(Integer.parseInt(slot), item);
                        }
                        kit.setItems(items);

                        if (kitsConfig.isSet("kits." + kit + ".permission")) {
                            String permission = kitsConfig.getString("kits." + kit + ".permission");
                            kit.setPermission(permission);
                        }
                        if (kitsConfig.isSet("kits." + kit + ".cooldown")) {
                            long cooldown = kitsConfig.getLong("kits." + kit + ".cooldown");
                            kit.setKitCooldown(cooldown);
                        }
                        if (kitsConfig.isSet("kits." + kit + ".cooldownBypass")) {
                            String cooldownBypass = kitsConfig.getString("kits." + kit + ".cooldownBypass");
                            kit.setCooldownBypassPermission(cooldownBypass);
                        }

                        return kit;
                    }

                    @Override
                    public void deserialize(Kit kit, @NotNull Fields fields) {
                        assert false;
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                })
        );
    }
}