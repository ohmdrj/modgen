package cz.test;

import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;

public class ModgenProperty {

    String name, type, etter, access;
    ArrayList<String> anots = new ArrayList<String>();

    ModgenProperty(ExecutableElement element) {
        etter = element.getSimpleName().toString().replaceFirst("get", "");
        type = element.getReturnType().toString();
        name = ModgenUtils.modCase(etter, false);
        if ("id".equals(name)) {
            anots.add("@GeneratedValue");
        } else if ("parent".equals(name)) {
            anots.add("@OneToMany");
        }
        anots.add("@AttrDesc(name=\"" + name + "\")");
        access = "public ";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEtter() {
        return etter;
    }

    public String getAccess() {
        return access;
    }

    public ArrayList<String> getAnots() {
        return anots;
    }

    @Override
    public String toString() {
        return name;
    }
}
