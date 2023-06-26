package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
