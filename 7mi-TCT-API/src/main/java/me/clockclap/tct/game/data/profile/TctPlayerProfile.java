package me.clockclap.tct.game.data.profile;

import me.clockclap.tct.NanamiTctApi;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class TctPlayerProfile {

    String name;
    boolean admin;

    public class TctPlayerProfileModifier extends TctProfileModifier {

        public TctPlayerProfileModifier(TctProperty<String, Object> defaults) {
            super(defaults);
        }

        @Override
        public void save() {
            if(super.property.get("name") instanceof String) {
                name = (String) super.property.get("name");
            } else {
                name = super.property.get("name").toString();
            }
            if(super.property.get("admin") instanceof Boolean) {
                admin = (Boolean) super.property.get("admin");
            } else {
                admin = false;
            }
        }
    }

    public TctPlayerProfile(String name) {
        this.name = NanamiTctApi.utilities.resetColor(name);
        this.admin = false;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAdmin() {
        if(NanamiTctApi.config.getConfig().getList("admin", Arrays.asList("op")).contains("op")) {
            return Bukkit.getPlayer(name).isOp() || NanamiTctApi.config.getConfig().getStringList("admin").contains(name);
        }
        return NanamiTctApi.config.getConfig().getList("admin", Arrays.asList("op")).contains(name);
    }

    public TctProfileModifier modify() {
        TctProperty<String, Object> p = new TctProperty<>();
        p.add("name", name);
        p.add("admin", admin);
        TctPlayerProfileModifier modifier = new TctPlayerProfileModifier(p);
        return modifier;
    }

}
