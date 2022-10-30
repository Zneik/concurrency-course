package course.concurrency.exams.auction;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class Notifier {

    private final Executor executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

    public void sendOutdatedMessage(Bid bid) {
        if (bid.getId() == Long.MIN_VALUE) {
            return;
        }
        executor.execute(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {}
}
