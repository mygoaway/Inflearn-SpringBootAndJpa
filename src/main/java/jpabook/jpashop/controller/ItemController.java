package jpabook.jpashop.controller;

import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "/items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setAuthor(form.getAuthor());
        book.setStockQuantity(form.getStockQuantity());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "/items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String createForm(@PathVariable Long itemId, Model model) {
        Book book = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setAuthor(book.getAuthor());
        form.setName(book.getName());
        form.setIsbn(book.getIsbn());
        form.setStockQuantity(book.getStockQuantity());
        form.setPrice(book.getPrice());

        model.addAttribute("form", form);
        return "/items/updateItemForm";
    }

    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
}
