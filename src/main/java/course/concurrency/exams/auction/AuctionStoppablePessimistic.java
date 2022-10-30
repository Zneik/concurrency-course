package course.concurrency.exams.auction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Lock lock;
    private volatile boolean isStopped;
    private final Notifier notifier;

    private Bid latestBid;
    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.lock = new ReentrantLock();
        this.isStopped = false;
    }

    public boolean propose(Bid bid) {
        try {
            lock.lock();
            if (isStopped) {
                return false;
            }
            if (latestBid == null) {
                latestBid = bid;
                return true;
            } else {
                if (bid.getPrice() > latestBid.getPrice()) {
                    Bid latestBidCopy = new Bid(latestBid.getId(),
                            latestBid.getParticipantId(), latestBid.getPrice());
                    notifier.sendOutdatedMessage(latestBidCopy);
                    latestBid = bid;
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Bid getLatestBid() {
        try {
            lock.lock();
            return latestBid;
        } finally {
            lock.unlock();
        }
    }

    public Bid stopAuction() {
        isStopped = true;
        return latestBid;
    }
}
