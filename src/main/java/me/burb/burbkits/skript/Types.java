package me.burb.burbkits.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.iterator.ListRangeIterator;
import ch.njol.yggdrasil.Fields;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.inventory.ItemStack;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class Types {
    public Types() {}

    static {
        Classes.registerClass(new ClassInfo<>(Kit.class, "kit")
                .user("kit?")
                .name("Kit")
                .description("Represents a Kit.")
                .examples("set {kit} to a new kit named \"Burb\"")
                .since("1.0.0")
                .parser(new Parser<Kit>() {
                    @Override
                    public String toString(Kit kit, int i) {
                        return kit.getName();
                    }

                    @Override
                    public String toVariableNameString(Kit kit) {
                        return kit.getName();
                    }
                })
                .serializer(new Serializer<Kit>() {
                    @Override
                    public Fields serialize(Kit kit) throws NotSerializableException {
                        Fields fields = new Fields();
                        Timespan time = new Timespan(kit.getKitCooldown());
                        List<ItemStack> list = new ArrayList<>(kit.getItems().values());
                        ListRangeIterator<ItemStack> skriptList = new ListRangeIterator<>(list, 0, 40);
                        fields.putObject("cooldown", time);
                        fields.putObject("items", skriptList);
                        fields.putObject("permission", kit.getPermission());
                        fields.putObject("name", kit.getName());
                        return fields;
                    }

                    @Override
                    public Kit deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                        return new Kit("abc");
                    }

                    @Override
                    public void deserialize(Kit kit, Fields fields) {
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