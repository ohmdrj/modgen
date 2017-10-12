package cz.test;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * @author by Ondřej Buriánek, burianek@marbes.cz.
 * @since 17.9.14
 */
public class ModgenUtils {

    public static String modCase(String string, boolean upper) {
        String first = Character.toString(string.charAt(0));
        return (upper ? first.toUpperCase() : first.toLowerCase()) + string.substring(1);
    }


    public static String packageNameOf(TypeElement type) {
        while (true) {
            Element enclosing = type.getEnclosingElement();
            if (enclosing instanceof PackageElement) {
                return ((PackageElement) enclosing).getQualifiedName().toString();
            }
            type = (TypeElement) enclosing;
        }
    }

    public static String classNameOf(TypeElement type) {
        String name = type.getQualifiedName().toString();
        String pkgName = packageNameOf(type);
        if (!pkgName.isEmpty()) {
            return name.substring(pkgName.length() + 1);
        } else {
            return name;
        }
    }

    public static String simplePackageOf(String s) {
        if (s.contains(".")) {
            return s.substring(0, s.lastIndexOf('.'));
        } else {
            return s;
        }
    }

    public static String simpleNameOf(String s) {
        if (s.contains(".")) {
            return s.substring(s.lastIndexOf('.') + 1);
        } else {
            return s;
        }
    }

}
