/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.lazyval;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Jatin
 */
@RunWith(Parameterized.class)
public class LazyValTest {
    
    public LazyValTest() {
    }
    
    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[20][0]);
    }

    /**
     * Test of getValue method, of class LazyVal.
     */
    @Test
    public void testGetValue() throws InterruptedException, BrokenBarrierException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final int threads = 50;
        CyclicBarrier b = new CyclicBarrier(threads);
        Set<String> ans = Collections.synchronizedSet(new HashSet<String>());
        LazyValImpl sample = new LazyValImpl();
        for(int i=0;i<threads-1;i++){
            exec.execute(new TestThread<String>(b,sample,ans));
        }
        b.await();
        Thread.sleep(1000);
        assertEquals(1, ans.size());
        //System.out.println(ans);
        exec.shutdown();
        //getDistinctValues
    }
    
     /**
     * Test of getValue method, of class DoubleIdiom.
     */
    @Test
    public void testDoubleIdiom() throws InterruptedException, BrokenBarrierException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final int threads = 50;
        CyclicBarrier b = new CyclicBarrier(threads);
        Set<String> ans = Collections.synchronizedSet(new HashSet<String>());
        DoubleImpl sample = new DoubleImpl();
        for(int i=0;i<threads-1;i++){
            exec.execute(new TestThread<String>(b,sample,ans));
        }
        b.await();
        Thread.sleep(1000);
        assertEquals(1, ans.size());
        exec.shutdown();
    }
    

    
    class TestThread<T> implements Runnable{
        private final CyclicBarrier latch;
        private final NewInstance<T> lazy;
        private final Set<T> ans;
        public TestThread(CyclicBarrier latch, NewInstance<T> lazy, Set<T> ans) {
            this.latch = latch;
            this.lazy = lazy;
            this.ans = ans;
        }
        
        @Override
        public void run() {
            try {
                latch.await();
            } catch (Exception ex) {
                Logger.getLogger(LazyValTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            T result = lazy.getValue();
            ans.add(result);
        }
        
    }
    
     class LazyValImpl extends LazyVal<String> {
    
        volatile int i =0;
        
        @Override
        protected String getInstance() {
            if(i>1) {
                throw new RuntimeException("Failure");
            }
            int x = (int)(Math.random()*50);
            try {
                Thread.sleep(x);
            } catch (InterruptedException ex) {
                Logger.getLogger(LazyValTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            return "string "+x;
        }
    }
     
     class DoubleImpl extends DoubleIdiom<String> {
    
        volatile int i =0;
        
        @Override
        protected String getInstance() {
            if(i>1) {
                throw new RuntimeException("Failure");
            }
            
            int x = (int)(Math.random()*50);
            try {
                Thread.sleep(x);
            } catch (InterruptedException ex) {
                Logger.getLogger(LazyValTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            return "string "+x;
        }
    }
}
