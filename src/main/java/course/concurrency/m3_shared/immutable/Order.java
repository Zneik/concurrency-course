package course.concurrency.m3_shared.immutable;

import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.IN_PROGRESS;
import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public static Order create(Long id, List<Item> items) {
        return new Order(id,
                items,
                null,
                false,
                NEW);
    }

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = List.copyOf(items);
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public boolean checkStatus() {
        if (!items.isEmpty() && paymentInfo != null && isPacked) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public Order withPacked(boolean packed) {
        return new Order(
                this.id,
                this.items,
                this.paymentInfo,
                packed,
                IN_PROGRESS
        );
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(
                this.id,
                this.items,
                paymentInfo,
                this.isPacked,
                this.status
        );
    }

    public Order withStatus(Status status) {
        return new Order(
                this.id,
                this.items,
                this.paymentInfo,
                this.isPacked,
                status
        );
    }

    public Status getStatus() {
        return status;
    }

}
