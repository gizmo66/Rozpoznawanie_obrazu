package Core;

import Extraction.Feature;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

@Slf4j
public class ReflectionUtils {

    public static Object invokeMethod(String methodName, Class<? extends Feature> clazz) {
        return invokeMethod(methodName, clazz, null);
    }

    public static Object invokeMethod(String methodName, Class<? extends Feature> clazz,
                                      Object[] args, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes).invoke(clazz.newInstance(), args);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            log.error(e.toString());
            throw new NotImplementedException();
        } catch (InvocationTargetException e) {
            log.error(e.getTargetException() + Arrays.toString(e.getTargetException().getStackTrace()));
            throw new NotImplementedException();
        }
    }

    public static Set<Class<? extends Feature>> getSubTypesOf(String packageName, Class<Feature> clazz) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(clazz);
    }
}
