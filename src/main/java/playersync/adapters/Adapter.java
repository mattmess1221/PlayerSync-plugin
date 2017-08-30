package playersync.adapters;

import com.google.common.reflect.TypeToken;

/**
 * Created by Matthew on 6/25/2017.
 */
public interface Adapter<Type> {

    TypeToken<? extends Adapter<Type>> getType();
}
