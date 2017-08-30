package playersync.adapters;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class Adapters {

    private static Map<Type, TypeToken<?>> tokens = Maps.newHashMap();
    private static Map<Type, Object> adapters = Maps.newHashMap();

    public static <A extends Adapter<?>> void register(Class<A> cl, A adapter) {
        adapters.put(adapter.getType().getType(), adapter);
        tokens.put(cl, adapter.getType());
    }

    @SuppressWarnings("unchecked")
    public static <T, A extends Adapter<T>> A get(TypeToken<A> cl) {
        return (A) adapters.get(cl.getType());
    }

    @SuppressWarnings("unchecked")
    static <T> TypeToken<T> getType(Class<?> cl) {
        return (TypeToken<T>) tokens.get(cl);
    }

}
