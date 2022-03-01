package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Item.Album;
import jpabook.jpashop.domain.Item.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Test
    @Rollback(false)
    public void 아이템_등록() throws Exception {
        // given
        Item item = new Album();
        item.setName("박효신");

        // when
        Long itemId = itemService.saveItem(item);

        // then
        assertEquals(item, itemService.findOne(itemId));
    }
}