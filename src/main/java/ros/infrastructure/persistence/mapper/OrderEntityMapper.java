package ros.infrastructure.persistence.mapper;

import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.model.OrderItem;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import ros.infrastructure.persistence.entity.OrderEntity;
import ros.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class OrderEntityMapper {

    public static MenuItemEntity toEntity(MenuItem model) {
        if (model == null) return null;
        return new MenuItemEntity(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getPrice(),
                model.getCategory(),
                model.getAvailable()
        );
    }

    public static MenuItem toDomain(MenuItemEntity entity) {
        if (entity == null) return null;
        return new MenuItem(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.getAvailable()
        );
    }

    public static OrderItemEntity toEntity(OrderItem model, OrderEntity orderEntity) {
        if (model == null) return null;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(model.getId());
        entity.setMenuItem(toEntity(model.getMenuItem()));
        entity.setQuantity(model.getQuantity());
        entity.setOrder(orderEntity);
        return entity;
    }

    public static OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;
        MenuItem menuItem = toDomain(entity.getMenuItem());
        OrderItem model = new OrderItem();
        model.setId(entity.getId());
        model.setMenuItem(menuItem);
        model.setPriceAtPurchase(menuItem != null ? menuItem.getPrice() : null);
        model.setQuantity(entity.getQuantity());
        return model;
    }

    public static OrderEntity toEntity(Order model) {
        if (model == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(model.getId());
        entity.setCustomerName(model.getCustomerName());
        entity.setTableNumber(model.getTableNumber());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        if (model.getItems() != null) {
            List<OrderItemEntity> itemEntities = model.getItems().stream()
                    .map(item -> toEntity(item, entity))
                    .collect(Collectors.toList());
            entity.setItems(itemEntities);
        }
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        Order model = new Order();
        model.setId(entity.getId());
        model.setCustomerName(entity.getCustomerName());
        model.setTableNumber(entity.getTableNumber());
        model.setStatus(entity.getStatus());
        model.setCreatedAt(entity.getCreatedAt());
        if (entity.getItems() != null) {
            List<OrderItem> itemModels = entity.getItems().stream()
                    .map(item -> {
                        OrderItem domainItem = toDomain(item);
                        domainItem.setOrder(model);
                        return domainItem;
                    })
                    .collect(Collectors.toList());
            model.setItems(itemModels);
        }
        return model;
    }
}
