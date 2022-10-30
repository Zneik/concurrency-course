package course.concurrency.exams.auction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private final Lock lock = new ReentrantLock();

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);

    public boolean propose(Bid bid) {
        if(bid.getPrice() <= latestBid.getPrice()) {
            return false;
        }
        Bid latestBidCopy = null;
        try {
            lock.lock();
            if (bid.getPrice() > latestBid.getPrice()) {
                latestBidCopy = new Bid(latestBid.getId(), latestBid.getParticipantId(), latestBid.getPrice());
                latestBid = bid;
            }
        } finally {
            lock.unlock();
        }
        if(latestBidCopy != null) {
            notifier.sendOutdatedMessage(latestBidCopy);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
