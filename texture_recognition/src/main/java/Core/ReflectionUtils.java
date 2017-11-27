package Core;

import Extraction.Feature;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class ReflectionUtils {

    public static Object invokeMethod(String methodName, Class<? extends Feature> clazz) {
        return invokeMethod(methodName, clazz, null);
    }

    public static Object invokeMethod(String methodName, Class<? extends Feature> clazz,
                               Object[] args, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes).invoke(clazz.newInstance(), args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            log.error(e.toString());
            throw new NotImplementedException();
        }
    }
}
