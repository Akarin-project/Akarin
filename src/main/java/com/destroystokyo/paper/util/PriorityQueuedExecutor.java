package com.destroystokyo.paper.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Implements an Executor Service that allows specifying Task Priority
 * and bumping of task priority.
 *
 * This is a non blocking executor with 3 priority levels.
 *
 * URGENT: Rarely used, something that is critical to take action now.
 * HIGH: Something with more importance than the base tasks
 *
 * @author Daniel Ennis &lt;aikar@aikar.co&gt;
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class PriorityQueuedExecutor extends AbstractExecutorService {

    private final ConcurrentLinkedQueue<Runnable> urgent = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Runnable> high = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Runnable> normal = new ConcurrentLinkedQueue<>();
    private final List<Thread> threads = new ArrayList<>();
    private final RejectionHandler handler;

    private volatile boolean shuttingDown = false;
    private volatile boolean shuttingDownNow = false;

    public PriorityQueuedExecutor(String name) {
        this(name, Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
    }

    public PriorityQueuedExecutor(String name, int threads) {
        this(name, threads, Thread.NORM_PRIORITY, null);
    }

    public PriorityQueuedExecutor(String name, int threads, int threadPriority) {
        this(name, threads, threadPriority, null);
    }

    public PriorityQueuedExecutor(String name, int threads, RejectionHandler handler) {
        this(name, threads, Thread.NORM_PRIORITY, handler);
    }

    public PriorityQueuedExecutor(String name, int threads, int threadPriority, RejectionHandler handler) {
        for (int i = 0; i < threads; i++) {
            ExecutorThread thread = new ExecutorThread(this::processQueues);
            thread.setDaemon(true);
            thread.setName(threads == 1 ? name  : name + "-" + (i + 1));
            thread.setPriority(threadPriority);
            thread.start();
            this.threads.add(thread);
        }
        if (handler == null) {
            handler = ABORT_POLICY;
        }
        this.handler = handler;
    }

    /**
     * If the Current thread belongs to a PriorityQueuedExecutor, return that Executro
     * @return The executor that controls this thread
     */
    public static PriorityQueuedExecutor getExecutor() {
        if (!(Thread.currentThread() instanceof ExecutorThread)) {
            return null;
        }
        return ((ExecutorThread) Thread.currentThread()).getExecutor();
    }

    public void shutdown() {
        shuttingDown = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Nonnull
    @Override
    public List<Runnable> shutdownNow() {
        shuttingDown = true;
        shuttingDownNow = true;
        List<Runnable> tasks = new ArrayList<>(high.size() + normal.size());
        Runnable run;
        while ((run = getTask()) != null) {
            tasks.add(run);
        }

        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return shuttingDown;
    }

    @Override
    public boolean isTerminated() {
        if (!shuttingDown) {
            return false;
        }
        return high.isEmpty() && normal.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, @Nonnull TimeUnit unit) {
        synchronized (this) {
            this.notifyAll();
        }
        final long wait = unit.toNanos(timeout);
        final long max = System.nanoTime() + wait;
        for (;!threads.isEmpty() && System.nanoTime() < max;) {
            threads.removeIf(thread -> !thread.isAlive());
        }
        return isTerminated();
    }


    public PendingTask<Void> createPendingTask(Runnable task) {
        return createPendingTask(task, Priority.NORMAL);
    }
    public PendingTask<Void> createPendingTask(Runnable task, Priority priority) {
        return createPendingTask(() -> {
            task.run();
            return null;
        }, priority);
    }

    public <T> PendingTask<T> createPendingTask(Supplier<T> task) {
        return createPendingTask(task, Priority.NORMAL);
    }

    public <T> PendingTask<T> createPendingTask(Supplier<T> task, Priority priority) {
        return new PendingTask<>(task, priority);
    }

    public PendingTask<Void> submitTask(Runnable run) {
        return createPendingTask(run).submit();
    }

    public PendingTask<Void> submitTask(Runnable run, Priority priority) {
        return createPendingTask(run, priority).submit();
    }

    public <T> PendingTask<T> submitTask(Supplier<T> run) {
        return createPendingTask(run).submit();
    }

    public <T> PendingTask<T> submitTask(Supplier<T> run, Priority priority) {
        PendingTask<T> task = createPendingTask(run, priority);
        return task.submit();
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        submitTask(command);
    }

    public boolean isCurrentThread() {
        final Thread thread = Thread.currentThread();
        if (!(thread instanceof ExecutorThread)) {
            return false;
        }
        return ((ExecutorThread) thread).getExecutor() == this;
    }

    public Runnable getUrgentTask() {
        return urgent.poll();
    }

    public Runnable getTask() {
        Runnable run = urgent.poll();
        if (run != null) {
            return run;
        }
        run = high.poll();
        if (run != null) {
            return run;
        }
        return normal.poll();
    }

    private void processQueues() {
        Runnable run = null;
        while (true) {
            if (run != null) {
                run.run();
            }
            if (shuttingDownNow) {
                return;
            }
            if ((run = getTask()) != null) {
                continue;
            }
            synchronized (PriorityQueuedExecutor.this) {
                if ((run = getTask()) != null) {
                    continue;
                }

                if (shuttingDown || shuttingDownNow) {
                    return;
                }
                try {
                    PriorityQueuedExecutor.this.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public boolean processUrgentTasks() {
        Runnable run;
        boolean hadTask = false;
        while ((run = getUrgentTask()) != null) {
            run.run();
            hadTask = true;
        }
        return hadTask;
    }

    public enum Priority {
        NORMAL, HIGH, URGENT
    }

    public class ExecutorThread extends Thread {
        public ExecutorThread(Runnable runnable) {
            super(runnable);
        }

        public PriorityQueuedExecutor getExecutor() {
            return PriorityQueuedExecutor.this;
        }
    }

    public class PendingTask <T> implements Runnable {

        private final AtomicBoolean hasRan = new AtomicBoolean();
        private final AtomicInteger submitted = new AtomicInteger(-1);
        private final AtomicInteger priority;
        private final Supplier<T> run;
        private final CompletableFuture<T> future = new CompletableFuture<>();
        private volatile PriorityQueuedExecutor executor;

        public PendingTask(Supplier<T> run) {
            this(run, Priority.NORMAL);
        }

        public PendingTask(Supplier<T> run, Priority priority) {
            this.priority = new AtomicInteger(priority.ordinal());
            this.run = run;
        }

        public boolean cancel() {
            return hasRan.compareAndSet(false, true);
        }

        @Override
        public void run() {
            if (!hasRan.compareAndSet(false, true)) {
                return;
            }

            try {
                future.complete(run.get());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        }

        public void bumpPriority() {
            bumpPriority(Priority.HIGH);
        }

        public void bumpPriority(Priority newPriority) {
            for (;;) {
                int current = this.priority.get();
                int ordinal = newPriority.ordinal();
                if (current >= ordinal || priority.compareAndSet(current, ordinal)) {
                    break;
                }
            }


            if (this.submitted.get() == -1 || this.hasRan.get()) {
                return;
            }

            // Only resubmit if it hasnt ran yet and has been submitted
            submit();
        }

        public CompletableFuture<T> onDone() {
            return future;
        }

        public PendingTask<T> submit() {
            if (shuttingDown) {
                handler.onRejection(this, PriorityQueuedExecutor.this);
                return this;
            }
            for (;;) {
                final int submitted = this.submitted.get();
                final int priority = this.priority.get();
                if (submitted == priority) {
                    return this;
                }
                if (this.submitted.compareAndSet(submitted, priority)) {
                    if (priority == Priority.URGENT.ordinal()) {
                        urgent.add(this);
                    } else if (priority == Priority.HIGH.ordinal()) {
                        high.add(this);
                    } else {
                        normal.add(this);
                    }

                    break;
                }
            }

            synchronized (PriorityQueuedExecutor.this) {
                // Wake up a thread to take this work
                PriorityQueuedExecutor.this.notify();
            }
            return this;
        }
    }
    public interface RejectionHandler {
        void onRejection(Runnable run, PriorityQueuedExecutor executor);
    }

    public static final RejectionHandler ABORT_POLICY = (run, executor) -> {
        throw new RejectedExecutionException("Executor has been shutdown");
    };
    public static final RejectionHandler CALLER_RUNS_POLICY = (run, executor) -> {
        run.run();
    };

}
