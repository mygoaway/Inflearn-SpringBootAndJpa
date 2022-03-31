package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;


/*
* xToOne(ManyToOne, OneToOne) 관계 최적화
* Order
* Order -> Member
* Order -> Delivery
*
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    /*
    * V1. 엔티티 직접 노출
    * - Hibernate5Module 모듈 드록, LAZY=null 처리
    * - 양방향 관계 문제 발생 -> @JsonIgnore
    * */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /*
    * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용 X)
    * - 단점: 지연로딩으로 쿼리 N번 호출
    * 주문내역이 2개인 경우, 1 + 2(회원) + 2(배송) 실행된다.
    * */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    /*
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용 O)
     * - fetch join으로 쿼리 1번 호출
     * */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

    /*
    * V4. JPA에서 DTO로 바로 조회
    * - 쿼리 1번 호출
    * - 장점 : select 절에서 원하는 데이터만 선택해서 조회(성능 최적화)
    * - 단점이라면, 재사용성이 부족함
    * */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderQueryRepository.findOrderDto();
    }

    /*
    *  쿼리 방식 석택 권장 순서
    * 1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
    * 2. 필요하면 fetch join으로 성능을 최적화 한다.
    * 3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
    * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.
    * */
}
