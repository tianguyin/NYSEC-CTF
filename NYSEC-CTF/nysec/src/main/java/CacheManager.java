import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static final Map<String, byte[]> fileCache = new HashMap<>();

    public static byte[] getFileFromCache(String filePath) {
        return fileCache.get(filePath);
    }

    public static void cacheFile(String filePath, byte[] fileData) {
        fileCache.put(filePath, fileData);
    }
}
