package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAll(OrderSearch ordersearch);
}
