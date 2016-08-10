package eu.merlevede.util;

import com.google.common.collect.ForwardingMap;
import eu.merlevede.flipit.simulator.actors.Player;

import javax.inject.Provider;
import java.util.Map;

/**
 * Created by Jonathan on 25/04/2016.
 */
public class DefaultMap<K,V> extends ForwardingMap<K,V> {
    private final Map<K, V> innerMap;
    private final Provider<V> defaultValueProvider;

    public DefaultMap(Map<K, V> innerMap, Provider<V> defaultValueProvider) {
        this.innerMap = innerMap;
        this.defaultValueProvider = defaultValueProvider;
    }
    @Override
    protected Map<K, V> delegate() {
        return innerMap;
    }
    @Override public boolean containsKey(Object key) {
        if (innerMap.containsKey(key)) {
            return true;
        } else {
            return key instanceof Player;
        }
    }
    @Override
    public V get(Object key) {
        return innerMap.getOrDefault(key, defaultValueProvider.get());
    }
}
