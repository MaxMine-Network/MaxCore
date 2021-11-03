//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginClassloader extends URLClassLoader {
    private static final Set<PluginClassloader> allLoaders = new CopyOnWriteArraySet<>();

    public PluginClassloader(URL[] urls) {
        super(urls);
        allLoaders.add(this);
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return this.loadClass0(name, resolve, true);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException var8) {
            if (checkOther) {
                Iterator<PluginClassloader> var4 = allLoaders.iterator();

                while(true) {
                    PluginClassloader loader;
                    do {
                        if (!var4.hasNext()) {
                            throw new ClassNotFoundException(name);
                        }

                        loader = var4.next();
                    } while(loader == this);

                    try {
                        return loader.loadClass0(name, resolve, false);
                    } catch (ClassNotFoundException var7) {
                    }
                }
            } else {
                throw new ClassNotFoundException(name);
            }
        }
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}
