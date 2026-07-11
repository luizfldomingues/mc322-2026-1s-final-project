package ros.application.service;

import ros.domain.repository.OrderRepository;
import ros.domain.model.Order;

import ros.application.dto.OrderCreationRequest;

public class OrderApplicationService {
    
    private OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //public Order createOrder(OrderCreationRequest request){

    //    return new Order();
    //}

}
