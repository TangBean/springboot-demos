package org.tangbean.springbucks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tangbean.springbucks.model.Coffee;
import org.tangbean.springbucks.model.CoffeeOrder;
import org.tangbean.springbucks.model.OrderState;
import org.tangbean.springbucks.repository.CoffeeOrderRepository;

import java.util.Arrays;

@Slf4j
@Service
//@Transactional
public class CoffeeOrderService {
    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    public CoffeeOrder createOrder(String customer, Coffee... coffee) {
        CoffeeOrder order = CoffeeOrder.builder()
                .customer(customer)
                .items(Arrays.asList(coffee))
                .state(OrderState.INIT)
                .build();
        CoffeeOrder saved = coffeeOrderRepository.insert(order);
        log.info("New order: {}", saved);
        return saved;
    }

    public boolean updateState(CoffeeOrder order, OrderState state) {
        // 订单状态机不能倒流
        if (state.compareTo(order.getState()) <= 0) {
            log.warn("Wrong State order: {}, {}", state, order.getState());
            return false;
        }

        order.setState(state);
        coffeeOrderRepository.save(order);
        log.info("Updated Order: {}", order);
        return true;
    }
}
