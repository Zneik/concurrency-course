package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        return shopIds.stream()
                .map(it -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, it))
                        .completeOnTimeout(Double.MAX_VALUE, 2900, TimeUnit.MILLISECONDS)
                        .handle((result, exception) -> Optional.ofNullable(result).orElse(Double.MAX_VALUE)))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .filter(it -> it != Double.MAX_VALUE)
                .min(Comparator.comparingDouble(it -> it))
                .orElse(Double.NaN);
    }

}
