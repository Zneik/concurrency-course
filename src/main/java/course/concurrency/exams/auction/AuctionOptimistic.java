package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;
public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(Long.MIN_VALUE,
            Long.MIN_VALUE, Long.MIN_VALUE));

    public boolean propose(Bid bid) {
        Bid expected;
        do {
            expected = latestBid.get();
            if (bid.getPrice() <= expected.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(expected, bid));
        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
