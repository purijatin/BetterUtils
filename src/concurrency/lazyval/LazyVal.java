package concurrency.lazyval;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 *
 * A lazy evaluation of a variable. Unlike standard DOubleIdiom, it does not hold a lock while initializing objects.
 * This is inspired from: SIP-20 (Scala Compiler Implementation)
 * 
 * @author Jatin
 */
public abstract class LazyVal<T> implements NewInstance<T>{

    protected abstract T getInstance();
    private volatile T result;
    private volatile int status = 0;
    private AtomicIntegerFieldUpdater<LazyVal> updater = AtomicIntegerFieldUpdater.newUpdater(LazyVal.class, "status");

    @Override
    public T getValue() {
        switch (updater.get(this)) {
            case 3:
                return result;
            case 0: {
                if (updater.compareAndSet(this, 0, 1)) {
                    result = getInstance();
                    switch (updater.get(this)) {
                        case 1: {
                            //nothing has happened. so relax
                            if (!updater.compareAndSet(this, 1, 3)) {
                                return getValue();
                            }
                            break;
                        }
                        case 2: {
                            if (!updater.compareAndSet(this, 2, 3)) {
                                return getValue();
                            }
                            synchronized (this) {
                                notifyAll();
                                break;
                            }
                        }
                        default:
                            throw new RuntimeException("How did this happen");
                    }
                    return result;
                } else {
                    return getValue();
                }
            }
            case 1: {
                updater.compareAndSet(this, 1, 2);
                synchronized (this) {
                    try {
                        while (updater.get(this) != 3) {
                            wait();
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread Interrupted");
                    }
                }
                return result;
            }
            case 2: {
                synchronized (this) {
                    try {
                        while (updater.get(this) != 3) {
                            wait();
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread Interrupted");
                    }
                }
                return result;
            }
            default:
                throw new RuntimeException("This should not happen");
        }
    }
}
