//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority;
    private final Map<Class<?>, EventHandlerMethod[]> byEventBaked;
    private final ReadWriteLock lock;
    private final Logger logger;

    public EventBus() {
        this(null);
    }

    public EventBus(Logger logger) {
        this.byListenerAndPriority = new HashMap();
        this.byEventBaked = new HashMap();
        this.lock = new ReentrantReadWriteLock();
        this.logger = logger == null ? Logger.getLogger("global") : logger;
    }

    public void post(Object event) {
        this.lock.readLock().lock();

        try {
            EventHandlerMethod[] handlers = this.byEventBaked.get(event.getClass());
            if (handlers != null) {
                EventHandlerMethod[] var3 = handlers;
                int var4 = handlers.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    EventHandlerMethod method = var3[var5];

                    try {
                        method.invoke(event);
                    } catch (IllegalAccessException var13) {
                        throw new Error("Method became inaccessible: " + event, var13);
                    } catch (IllegalArgumentException var14) {
                        throw new Error("Method rejected target/argument: " + event, var14);
                    } catch (InvocationTargetException var15) {
                        this.logger.log(Level.WARNING, MessageFormat.format("Error dispatching event {0} to listener {1}", event, method.getListener()), var15.getCause());
                    }
                }
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap();
        Method[] var3 = listener.getClass().getDeclaredMethods();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Method m = var3[var5];
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    this.logger.log(Level.INFO, "Method {0} in class {1} annotated with {2} does not have single argument", new Object[]{m, listener.getClass(), annotation});
                } else {
                    Map<Byte, Set<Method>> prioritiesMap = handler.get(params[0]);
                    if (prioritiesMap == null) {
                        prioritiesMap = new HashMap();
                        handler.put(params[0], prioritiesMap);
                    }

                    Set<Method> priority = (Set)((Map)prioritiesMap).get(annotation.priority());
                    if (priority == null) {
                        priority = new HashSet();
                        ((Map)prioritiesMap).put(annotation.priority(), priority);
                    }

                    priority.add(m);
                }
            }
        }

        return handler;
    }

    public void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers(listener);
        this.lock.writeLock().lock();

        try {
            Iterator var3 = handler.entrySet().iterator();

            while(var3.hasNext()) {
                Entry<Class<?>, Map<Byte, Set<Method>>> e = (Entry)var3.next();
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
                if (prioritiesMap == null) {
                    prioritiesMap = new HashMap();
                    this.byListenerAndPriority.put(e.getKey(), prioritiesMap);
                }

                Iterator var6 = ((Map)e.getValue()).entrySet().iterator();

                while(var6.hasNext()) {
                    Entry<Byte, Set<Method>> entry = (Entry)var6.next();
                    Map<Object, Method[]> currentPriorityMap = (Map)((Map)prioritiesMap).get(entry.getKey());
                    if (currentPriorityMap == null) {
                        currentPriorityMap = new HashMap();
                        ((Map)prioritiesMap).put(entry.getKey(), currentPriorityMap);
                    }

                    Method[] baked = new Method[entry.getValue().size()];
                    ((Map)currentPriorityMap).put(listener, ((Set)entry.getValue()).toArray(baked));
                }

                this.bakeHandlers(e.getKey());
            }
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    public void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers(listener);
        this.lock.writeLock().lock();

        Entry e;
        try {
            for(Iterator var3 = handler.entrySet().iterator(); var3.hasNext(); this.bakeHandlers((Class)e.getKey())) {
                e = (Entry)var3.next();
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
                if (prioritiesMap != null) {
                    Iterator var6 = ((Map)e.getValue()).keySet().iterator();

                    while(var6.hasNext()) {
                        Byte priority = (Byte)var6.next();
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(priority);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (currentPriority.isEmpty()) {
                                prioritiesMap.remove(priority);
                            }
                        }
                    }

                    if (prioritiesMap.isEmpty()) {
                        this.byListenerAndPriority.remove(e.getKey());
                    }
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    private void bakeHandlers(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> handlersByPriority = this.byListenerAndPriority.get(eventClass);
        if (handlersByPriority != null) {
            List<EventHandlerMethod> handlersList = new ArrayList(handlersByPriority.size() * 2);
            byte value = -128;

            do {
                Map<Object, Method[]> handlersByListener = handlersByPriority.get(value);
                if (handlersByListener != null) {
                    Iterator var6 = handlersByListener.entrySet().iterator();

                    while(var6.hasNext()) {
                        Entry<Object, Method[]> listenerHandlers = (Entry)var6.next();
                        Method[] var8 = listenerHandlers.getValue();
                        int var9 = var8.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            Method method = var8[var10];
                            EventHandlerMethod ehm = new EventHandlerMethod(listenerHandlers.getKey(), method);
                            handlersList.add(ehm);
                        }
                    }
                }
            } while(value++ < 127);

            this.byEventBaked.put(eventClass, handlersList.toArray(new EventHandlerMethod[handlersList.size()]));
        } else {
            this.byEventBaked.put(eventClass, null);
        }

    }
}
