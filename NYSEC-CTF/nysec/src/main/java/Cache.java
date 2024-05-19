import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> {
    private final int maxSize;
    final Map<K, CacheItem<V>> cacheMap;

    public Cache(int maxSize) {
        this.maxSize = maxSize;
        this.cacheMap = new LinkedHashMap<K, CacheItem<V>>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheItem<V>> eldest) {
                return size() > maxSize;
            }
        };
    }

    public V get(K key) {
        CacheItem<V> item = cacheMap.get(key);
        if (item != null && !item.isExpired()) {
            return item.getValue();
        }
        return null;
    }

    public void put(K key, V value, long ttl, long lastModified) {
        cacheMap.put(key, new CacheItem<>(value, ttl, lastModified));
    }

    public void clear() {
        cacheMap.clear();
    }

    static class CacheItem<V> {
        private final V value;
        private final long expiryTime;
        private final long lastModified;

        CacheItem(V value, long ttl, long lastModified) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttl;
            this.lastModified = lastModified;
        }

        V getValue() {
            return value;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        long getLastModified() {
            return lastModified;
        }
    }
}
