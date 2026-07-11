package ros.domain.model.Filters;

import java.time.LocalDateTime;
import java.util.List;

import ros.domain.model.OrderStatus;

public class OrderFilter {
    private LocalDateTime fromData;
    private LocalDateTime toDate;
    private List<OrderStatus> statuses;
    private Long minValue;

    public OrderFilter(LocalDateTime fromData, LocalDateTime toDate, List<OrderStatus> statuses, Long minValue) {
        this.fromData = fromData;
        this.toDate = toDate;
        this.statuses = statuses;
        this.minValue = minValue;
    }
}
