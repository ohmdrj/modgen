package cz.test;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class ModgenTemplate {

    List<ModgenProperty> props = new ArrayList<ModgenProperty>();
    SortedSet<String> impos = new TreeSet<String>();
    String pkg;
    String base;
    String synth;

    @Override
    public String toString() {
        VelocityContext velocityContext = toVelocityContext();
        StringWriter writer = new StringWriter();
        SimpleNode parsedTemplate = parsedTemplateForResource("/modgen.vm");
        boolean rendered = velocityRuntimeInstance.render(
                velocityContext, writer, parsedTemplate.getTemplateName(), parsedTemplate);
        if (!rendered) {
            throw new IllegalArgumentException("Template rendering failed");
        }
        return writer.toString();
    }

    private VelocityContext toVelocityContext() {
        VelocityContext velocityContext = new VelocityContext();
        for (Field field : getClass().getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (field.isSynthetic() || (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers))) {
                continue;
            }
            if (Modifier.isPrivate(modifiers) | Modifier.isStatic(modifiers) | field.getType().isPrimitive()) {
                throw new IllegalArgumentException("Invalid field: " + field);
            }
            try {
                Object value = field.get(this);
                velocityContext.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return velocityContext;
    }

    private static final RuntimeInstance velocityRuntimeInstance = new RuntimeInstance();

    static {
        //velocityRuntimeInstance.setProperty(RuntimeConstants.RUNTIME_REFERENCES_STRICT, "true");
        Thread currentThread = Thread.currentThread();
        ClassLoader oldContextLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(ModgenTemplate.class.getClassLoader());
            velocityRuntimeInstance.init();
        } finally {
            currentThread.setContextClassLoader(oldContextLoader);
        }
    }

    static SimpleNode parsedTemplateForResource(String resourceName) {
        InputStream in = ModgenTemplate.class.getResourceAsStream(resourceName);
        if (in == null) {
            throw new IllegalArgumentException("Could not find resource: " + resourceName);
        }
        try {
            Reader reader = new InputStreamReader(in, "UTF-8");
            return velocityRuntimeInstance.parse(reader, resourceName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
    }

    static Object fieldValue(Field field, Object container) {
        try {
            return field.get(container);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }
}
