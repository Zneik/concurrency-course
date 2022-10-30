package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    private final AtomicMarkableReference<Bid> latestBid;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L), false);
    }

    public boolean propose(Bid bid) {
        Bid expected;
        Bid updated;
        do {
            expected = latestBid.getReference();
            if ((expected == null)) {
                updated = bid;
            } else {
                if (bid.getPrice() > expected.getPrice()) {
                    updated = bid;
                } else {
                    updated = expected;
                }
            }
        } while (!latestBid.compareAndSet(expected, updated, false, false));
        if (expected == null) {
            return true;
        } else {
            if (!updated.equals(expected)) {
                notifier.sendOutdatedMessage(expected);
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid expected;
        Bid updated;
        do {
            expected = latestBid.getReference();
            updated = expected;
        } while (!latestBid.compareAndSet(expected, updated, false, true));
        return latestBid.getReference();
    }
}
