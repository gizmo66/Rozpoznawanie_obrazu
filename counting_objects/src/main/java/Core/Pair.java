package Core;

import lombok.Getter;

@Getter
public class Pair<T> {

    private T p1, p2;

    public Pair(T p1, T p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
