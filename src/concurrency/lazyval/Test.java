/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.lazyval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jatin
 */
public class Test {

    public static void main(String... args) throws Exception {
        Test t = new Test();
        t.compareTimeTaken();
    }

    private void compareTimeTaken() throws Exception {
        /**
         * This is the time it will take for variable to be initialized
         */
        final int x = 10000;//(int)(Math.random()*10000);
        System.out.println("-------LazyVal------");
        test(new Sample(x));
         System.out.println("-------Double Idiom------");
        test(new Sample2(x));
        
    }

    private <T> void test(NewInstance<T> n) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        final int threads = 50;
        CyclicBarrier b = new CyclicBarrier(threads);
        List<Long> ans = Collections.synchronizedList(new ArrayList<Long>());
        for (int i = 0; i < threads - 1; i++) {
            exec.execute(new TestThread<>(b, n, ans));
        }
        b.await();
        Collections.sort(ans);
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Minimum time(ns) taken: " + ans.get(0));
        System.out.println("Maximum time(ns) taken: " + ans.get(ans.size() - 1));
        System.out.println("Average time(ns) taken: " + calculateAverage(ans));
    }

    private double calculateAverage(List<Long> marks) {
        Long sum = 0L;
        if (!marks.isEmpty()) {
            for (Long mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }
}

class TestThread<T> implements Runnable {

    private final CyclicBarrier latch;
    private final NewInstance<T> lazy;
    private final List<Long> ans;

    public TestThread(CyclicBarrier latch, NewInstance<T> lazy, List<Long> ans) {
        this.latch = latch;
        this.lazy = lazy;
        this.ans = ans;
    }

    @Override
    public void run() {
        try {
            latch.await();
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        long st = System.nanoTime();
        T result = lazy.getValue();
        long resilt = System.nanoTime() - st;
        ans.add(resilt);
    }
}

class Sample extends LazyVal<String> {
    final int x;
    Sample(int x){
        this.x = x;
    }
    volatile int i = 0;

    @Override
    protected String getInstance() {
        if (i > 1) {
            throw new RuntimeException("Failure");
        }
        try {
            Thread.currentThread().sleep(x);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        i++;
        return "string " + x;
    }
}

class Sample2 extends DoubleIdiom<String> {

    volatile int i = 0;

    final int x;
    Sample2(int x){
        this.x = x;
    }
    @Override
    protected String getInstance() {
        if (i > 1) {
            throw new RuntimeException("Failure");
        }
        
        try {
            Thread.currentThread().sleep(x);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        i++;
        return "string " + x;
    }
}
