package dev.quatern.marketplace.enums;

import java.util.Map;
import java.util.Set;

public enum OrderStatusEnum {

    CREATED,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED;

    private static final Map<OrderStatusEnum, Set<OrderStatusEnum>> ALLOWED_TRANSITIONS =
        Map.of(
            CREATED, Set.of(PAID, CANCELED),
            PAID, Set.of(SHIPPED, CANCELED),
            SHIPPED, Set.of(COMPLETED),
            COMPLETED, Set.of(),
            CANCELED, Set.of()
        );

    public boolean canTransitionTo(OrderStatusEnum next) {
        return ALLOWED_TRANSITIONS.get(this).contains(next);
    }

}
