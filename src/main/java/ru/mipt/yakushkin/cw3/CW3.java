package ru.mipt.yakushkin.cw3;


import org.junit.jupiter.api.Assertions;
import ru.mipt.yakushkin.cw3.bst.par.BstPar;
import ru.mipt.yakushkin.cw3.bst.seq.BstSeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CW3 {
    public static final AtomicInteger ops = new AtomicInteger();
    private static final ReentrantLock mutex = new ReentrantLock();
    private static final int N_THREADS = 4;
    private static final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
    private static final Random random = new Random(42);
    private static final boolean NEED_CHECK = true;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int numThreads = 1; numThreads <= N_THREADS; numThreads++) {
            System.out.println("Threads count: " + numThreads);
            for (Integer x : List.of(0, 10, 50)) {
                BstSeq<Integer> bstSeq = new BstSeq<>(Integer.MAX_VALUE);
                BstPar<Integer> bstPar = new BstPar<>(Integer.MAX_VALUE);


                List<Integer> keys = IntStream.range(1, (int) 1e5 + 1).boxed()
                                              .collect(Collectors.toList());

                Collections.shuffle(keys);

                keys.forEach(k -> {
                    if (random.nextInt(2) == 0) {
                        bstSeq.insert(k);
                        bstPar.insert(k);
                    }
                });

                List<CompletableFuture<Void>> futures = new ArrayList<>();

                long start = System.currentTimeMillis();
                long deadline = System.currentTimeMillis() + 5000;

                for (int i = 0; i < numThreads; i++) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                            () -> performOperations(x, bstSeq, bstPar, keys, deadline),
                            executor
                    );
                    futures.add(completableFuture);
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                 .exceptionally(e -> {
                                     System.out.println("Error during completable future " + e);
                                     return null;
                                 }).get();

                double ops = CW3.ops.get() * 1.0 / (System.currentTimeMillis() - start) * 1000;
                CW3.ops.set(0);

                System.out.println("X: " + x + "\tOPS: " + String.format("%.2f", ops));

                List<Integer> nodePars = bstPar.inorderTraversal();
                List<Integer> nodeParsCopy = new ArrayList<>(nodePars);
                Assertions.assertEquals(nodePars, nodeParsCopy);

                if (NEED_CHECK) {
                    List<Integer> nodeSeqs = bstSeq.inorderTraversal();
                    List<Integer> nodeSeqsCopy = new ArrayList<>(nodeSeqs);
                    Collections.sort(nodeSeqsCopy);
                    Assertions.assertEquals(nodeSeqs, nodeSeqsCopy);
                    Assertions.assertEquals(nodePars, nodeSeqs);
                }

            }
            System.out.println();
        }

        executor.shutdown();

    }

    private static void performOperations(Integer x, BstSeq<Integer> bstSeq, BstPar<Integer> bstPar, List<Integer> keys, long deadline) {
        while (System.currentTimeMillis() <= deadline) {
            Integer key = keys.get(random.nextInt(keys.size()));
            int p = random.nextInt(101);
            if (p < x) {
                if (NEED_CHECK) {
                    mutex.lock();
                    bstSeq.insert(key);
                    mutex.unlock();
                }

                bstPar.insert(key);
            } else if (p >= x && p < 2 * x) {
                if (NEED_CHECK) {
                    mutex.lock();
                    bstSeq.delete(key);
                    mutex.unlock();
                }

                bstPar.delete(key);
            } else if (p >= 2 * x && p <= 100) {
                if (NEED_CHECK) {
                    mutex.lock();
                    bstSeq.contains(key);
                    mutex.unlock();
                }

                bstPar.contains(key);
            }
            ops.getAndIncrement();
        }
    }
}