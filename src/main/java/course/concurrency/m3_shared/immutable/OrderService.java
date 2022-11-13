package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    private synchronized long nextId() {
        return nextId.getAndIncrement();
    }

    public synchronized long createOrder(List<Item> items) {
        long id = nextId();
        Order order = Order.create(id, items);
        currentOrders.put(id, order);
        return id;
    }

    public synchronized void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        currentOrders.compute(orderId, (k, v) -> v.withPaymentInfo(paymentInfo));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    public synchronized void setPacked(long orderId) {
        currentOrders.compute(orderId, (k, v) -> v.withPacked(true));
        if (currentOrders.get(orderId).checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    private synchronized void deliver(Order order) {
        currentOrders.compute(order.getId(), (k, v) -> v.withStatus(Order.Status.DELIVERED));
    }

    public synchronized boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
