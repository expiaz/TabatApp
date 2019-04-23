package expiaz.tabata.utils;

public interface Callbackable<R, P> {
    R call(P p);
}
