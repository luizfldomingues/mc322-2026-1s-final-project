package ros.domain.model.Filters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import ros.domain.model.OrderStatus;

public class OrderFilter {
    private LocalDateTime fromDate;   
    private LocalDateTime toDate;
    private List<OrderStatus> statuses;
    private Double minValue;

    public OrderFilter(LocalDateTime fromDate, LocalDateTime toDate, List<OrderStatus> statuses, Double minValue) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        if (statuses == null) this.statuses = new ArrayList<>();
        else this.statuses = new ArrayList<>(statuses);
        this.minValue = minValue;
    }

}
