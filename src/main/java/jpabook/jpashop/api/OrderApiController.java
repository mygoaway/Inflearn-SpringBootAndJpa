package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리 * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화 order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제 초기환
        }
        return all;
    }

    /**
     * V2. 엔티티를 DTO로 변환
     * N + 1 문제 발생(생각하지 못한 추가 쿼리 발생)
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = all.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }

    /**
     * V3. 엔티티를 DTO로 변환 - 패치 조인 최적화
     * 패치 조인으로 SQL이 1번만 실행됨
     * 하지만, 일대다인 경우에는 페이징 불가능
     * 그리고 컬렉션 패치 조인은 1개만 사용할 수 있다. why? 컬렉션 둘 이상에 패치 조인을 사용하면 데이터 뻥튀기가 더 심화됨
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDto> result = all.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }

    /**
     * V3.1 엔티티를 DTO로 변환 - 페이징과 한계 돌파
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "0") int limit) {
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = all.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return result;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItem;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItem = order.getOrderItems().stream().map(o->new OrderItemDto(o)).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}