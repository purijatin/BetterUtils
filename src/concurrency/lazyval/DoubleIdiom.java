/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.lazyval;

/**
 *
 * @author Jatin
 */
public abstract class DoubleIdiom<T> implements NewInstance<T>{

    protected abstract T getInstance();
    private volatile T result;

    @Override
    public T getValue() {
        if (result != null) {
            return result;
        } else {
            synchronized (this) {
                if (result == null) {
                    result = getInstance();
                    return result;
                } else {
                    return result;
                }
            }
        }
    }
}
