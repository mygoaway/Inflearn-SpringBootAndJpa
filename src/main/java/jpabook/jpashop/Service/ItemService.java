package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    @Transactional
    public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
        // JPA의 변경감지기능 사용(Dirty Checking)
        Item findItem = itemRepository.findById(itemId).get();
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        return findItem;
    }

    public Item findOne(Long id) {
        return itemRepository.findById(id).get();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }
}
