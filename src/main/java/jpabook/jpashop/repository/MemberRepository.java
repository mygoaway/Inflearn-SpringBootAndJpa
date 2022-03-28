package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // select m from Member m where m.name = ?
    List<Member> findByName(String name);
}
