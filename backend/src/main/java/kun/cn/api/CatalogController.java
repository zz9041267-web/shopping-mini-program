package kun.cn.api;

import kun.cn.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin
public class CatalogController {
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final ShopProfileRepository shopProfiles;

    public CatalogController(CategoryRepository c, ProductRepository p, ShopProfileRepository s) {
        categories = c;
        products = p;
        shopProfiles = s;
    }

    @GetMapping
    public Map<String, Object> catalog() {
        var list = categories.findAllByOrderBySortOrderAsc();
        var grouped = new LinkedHashMap<Long, Object>();
        var visibleCategories = new ArrayList<Object>();
        for (var c : list) {
            var categoryProducts = products.findByCategoryIdAndOnSaleTrueOrderByIdDesc(c.getId());
            if (!categoryProducts.isEmpty()) { grouped.put(c.getId(), categoryProducts); visibleCategories.add(c); }
        }
        var shop = shopProfiles.findById(1L).orElseGet(() -> shopProfiles.save(new kun.cn.domain.ShopProfile()));
        return Map.of("shop", shop, "categories", visibleCategories, "products", grouped);
    }
}
