//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.event.EventBus;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class PluginManager {
    private static final Pattern argsSplit = Pattern.compile(" ");
    private final MaxMineCore core;
    private final Yaml yaml;
    private final EventBus eventBus;
    private final Map<String, Plugin> plugins = new LinkedHashMap<>();
    private Map<String, PluginDescription> toLoad = new HashMap<>();
    private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();

    public PluginManager(MaxMineCore core) {
        this.core = core;
        Constructor yamlConstructor = new Constructor();
        PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        yamlConstructor.setPropertyUtils(propertyUtils);
        this.yaml = new Yaml(yamlConstructor);
        this.eventBus = new EventBus(core.getLogger());
    }

    public Collection<Plugin> getPlugins() {
        return this.plugins.values();
    }

    public Plugin getPlugin(String name) {
        return this.plugins.get(name);
    }

    public void loadPlugins() {
        Map<PluginDescription, Boolean> pluginStatuses = new HashMap<>();

        toLoad.forEach((key, value) -> {
            if (!enablePlugin(pluginStatuses, new Stack<>(), value)) {
                core.getLogger().log(Level.WARNING, "Failed to enable {0}", key);
            }
        });

        this.toLoad.clear();
        this.toLoad = null;
    }

    public void enablePlugins() {
        for (Plugin plugin : plugins.values()) {
            try {
                plugin.onEnable();
                this.core.getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[]
                        {
                                plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor()
                        });
            } catch (Throwable t) {
                this.core.getLogger().log(Level.WARNING, "Exception encountered when loading plugin: " + plugin.getDescription().getName(), t);
            }
        }
    }

    private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack, PluginDescription plugin) {
        if (pluginStatuses.containsKey(plugin)) {
            return pluginStatuses.get(plugin);
        }

        // combine all dependencies for 'for loop'
        Set<String> dependencies = new HashSet<>();
        dependencies.addAll(plugin.getDepends());
        dependencies.addAll(plugin.getSoftDepends());

        // success status
        boolean status = true;

        // try to load dependencies first
        for (String dependName : dependencies) {
            PluginDescription depend = toLoad.get(dependName);
            Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;

            if (dependStatus == null) {
                if (dependStack.contains(depend)) {
                    StringBuilder dependencyGraph = new StringBuilder();
                    for (PluginDescription element : dependStack) {
                        dependencyGraph.append(element.getName()).append(" -> ");
                    }
                    dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
                    this.core.getLogger().log(Level.WARNING, "Circular dependency detected: {0}", dependencyGraph);
                    status = false;
                } else {
                    dependStack.push(plugin);
                    dependStatus = this.enablePlugin(pluginStatuses, dependStack, depend);
                    dependStack.pop();
                }
            }

            if (dependStatus == Boolean.FALSE && plugin.getDepends().contains(dependName)) // only fail if this wasn't a soft dependency
            {
                this.core.getLogger().log(Level.WARNING, "{0} (required by {1}) is unavailable", new Object[]
                        {
                                String.valueOf(dependName), plugin.getName()
                        });
                status = false;
            }

            if (!status) {
                break;
            }
        }

        // do actual loading
        if (status) {
            try {
                URLClassLoader loader = new PluginClassloader(new URL[]
                        {
                                plugin.getFile().toURI().toURL()
                        });
                Class<?> main = loader.loadClass(plugin.getMain());
                Plugin clazz = (Plugin) main.getDeclaredConstructor().newInstance();

                clazz.init(core, plugin);
                plugins.put(plugin.getName(), clazz);
                clazz.onLoad();
                this.core.getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[]
                        {
                                plugin.getName(), plugin.getVersion(), plugin.getAuthor()
                        });
            } catch (Throwable t) {
                this.core.getLogger().log(Level.WARNING, "Error enabling plugin " + plugin.getName(), t);
            }
        }

        pluginStatuses.put(plugin, status);
        return status;
    }

    public void detectPlugins(File folder) {
        Preconditions.checkNotNull(folder, "folder");
        Preconditions.checkArgument(folder.isDirectory(), "Must load from a directory");

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(file)) {
                    JarEntry pdf = jar.getJarEntry("core.yml");
                    if (pdf == null) {
                        pdf = jar.getJarEntry("plugin.yml");
                    }
                    Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml or core.yml");

                    try (InputStream in = jar.getInputStream(pdf)) {
                        PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
                        Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
                        Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);

                        desc.setFile(file);
                        toLoad.put(desc.getName(), desc);
                    }
                } catch (Exception ex) {
                    this.core.getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex);
                }
            }
        }
    }

    public <T extends Event> T callEvent(T event) {
        Preconditions.checkNotNull(event, "event");
        long start = System.nanoTime();
        this.eventBus.post(event);
        event.postCall();
        long elapsed = start - System.nanoTime();
        if (elapsed > 250000L) {
            this.core.getLogger().log(Level.WARNING, "Event {0} took more {1}ns to process!", new Object[]{event, elapsed});
        }

        return event;
    }

    public void registerListener(Plugin plugin, Listener listener) {
        for ( Method method : listener.getClass().getDeclaredMethods() )
        {
            Preconditions.checkArgument( !method.isAnnotationPresent( Subscribe.class ),
                    "Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.", listener );
        }
        eventBus.register( listener );
        listenersByPlugin.put( plugin, listener );
    }

    public void registerListener(Listener listener) {
        this.eventBus.register(listener);
    }

    public void unregisterListener(Listener listener) {
        eventBus.unregister( listener );
        listenersByPlugin.values().remove( listener );
    }

    public void unregisterListeners(Plugin plugin) {
        for ( Iterator<Listener> it = listenersByPlugin.get( plugin ).iterator(); it.hasNext(); )
        {
            eventBus.unregister( it.next() );
            it.remove();
        }
    }
}
