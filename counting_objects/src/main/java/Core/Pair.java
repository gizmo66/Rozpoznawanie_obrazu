package Core;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode()
public class Pair<T> {

    public T x, y;

    public Pair(T x, T y) {
        this.x = x;
        this.y = y;
    }
}
