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

    public OrderFilter() {}

    public OrderFilter(LocalDateTime fromDate, LocalDateTime toDate, List<OrderStatus> statuses, Double minValue) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        if (statuses == null) this.statuses = new ArrayList<>();
        else this.statuses = new ArrayList<>(statuses);
        this.minValue = minValue;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    public List<OrderStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<OrderStatus> statuses) {
        this.statuses = statuses;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }
}
