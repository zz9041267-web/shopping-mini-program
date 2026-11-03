package kun.cn.config;

import kun.cn.domain.*;
import kun.cn.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner init(CategoryRepository categories, ProductRepository products, ShopProfileRepository shops, SizeSetRepository sizeSets, ColorSetRepository colorSets) {
        return args -> {
            var defaultSizeSet = sizeSets.findAll().stream().findFirst().orElseGet(() -> {
                var set = new SizeSet();
                set.setNameZh("常规尺码"); set.setNameEn("Standard sizes");
                set.setOptions(java.util.List.of(new SizeOption("90", 38, 30), new SizeOption("100", 41, 32), new SizeOption("110", 44, 34), new SizeOption("120", 47, 36)));
                return sizeSets.save(set);
            });
            var defaultColorSet = colorSets.findAll().stream().findFirst().orElseGet(() -> {
                var set = new ColorSet();
                set.setNameZh("常规颜色"); set.setNameEn("Standard colors");
                set.setOptions(java.util.List.of(new ColorOption("pink", "粉色", "Pink", "#F5A6B8"), new ColorOption("blue", "蓝色", "Blue", "#7DA7D9"), new ColorOption("beige", "米白", "Cream", "#E9D5B6")));
                return colorSets.save(set);
            });
            var shop = shops.findById(1L).orElseGet(() -> shops.save(new ShopProfile()));
            if (blank(shop.getNameZh())) { shop.setNameZh(shop.getName()); shop.setNameEn("Jian'erbao Kidswear"); shop.setNoticeZh(shop.getNotice()); shop.setNoticeEn("Children's clothing wholesale. Style selection and bulk orders welcome."); shops.save(shop); }
            categories.findAll().forEach(category -> { if (blank(category.getNameZh())) { category.setNameZh(category.getName()); category.setNameEn(category.getName()); categories.save(category); } });
            products.findAll().forEach(product -> {
                boolean changed = false;
                if (blank(product.getNameZh())) { product.setNameZh(product.getName()); product.setNameEn(product.getName()); product.setDescriptionZh(product.getDescription()); product.setDescriptionEn(product.getDescription()); changed = true; }
                if (product.getDisplayImagesBase64().isEmpty() && !blank(product.getImageBase64())) { product.setDisplayImagesBase64(java.util.List.of(product.getImageBase64())); changed = true; }
                if (product.getSizeSet() == null) { product.setSizeSet(defaultSizeSet); changed = true; }
                if (product.getColorSet() == null) { product.setColorSet(defaultColorSet); changed = true; }
                if (changed) products.save(product);
            });
            migrateLegacyFoodSamples(categories, products);
            if (categories.count() > 0) return;
            var newArrivals = categories.save(new Category("新品到货", "New Arrivals", 1));
            var girls = categories.save(new Category("女童精选", "Girls", 2));
            var boys = categories.save(new Category("男童精选", "Boys", 3));
            add(products, newArrivals, defaultSizeSet, defaultColorSet, "纯棉印花连衣裙", "Cotton Print Dress", "柔软纯棉面料，适合春夏日常穿搭", "Soft cotton dress for everyday spring and summer wear.", new BigDecimal("59.00"), 88);
            add(products, newArrivals, defaultSizeSet, defaultColorSet, "韩版儿童套装", "Korean Style Kids Set", "上衣与短裤成套搭配，方便批量选款", "Matching top and shorts set for easy bulk selection.", new BigDecimal("79.00"), 66);
            add(products, girls, defaultSizeSet, defaultColorSet, "儿童加绒卫衣", "Fleece Kids Sweatshirt", "内里加绒保暖，宽松舒适不束缚", "Warm fleece lining with a relaxed, comfortable fit.", new BigDecimal("49.00"), 40);
            add(products, boys, defaultSizeSet, defaultColorSet, "宝宝针织开衫", "Baby Knit Cardigan", "亲肤针织，适合叠穿和换季备货", "Soft knit cardigan for layering and seasonal stocking.", new BigDecimal("45.00"), 100);
        };
    }

    private void add(ProductRepository products, Category category, SizeSet sizeSet, ColorSet colorSet, String nameZh, String nameEn, String descriptionZh, String descriptionEn, BigDecimal price, int stock) {
        var product = new Product();
        product.setCategory(category); product.setNameZh(nameZh); product.setNameEn(nameEn); product.setDescriptionZh(descriptionZh); product.setDescriptionEn(descriptionEn);
        product.setName(nameZh); product.setDescription(descriptionZh); product.setPrice(price); product.setStock(stock); product.setOnSale(true); product.setSizeSet(sizeSet); product.setColorSet(colorSet); products.save(product);
    }

    // Converts only the exact legacy demo records; administrator-created data is left untouched.
    private void migrateLegacyFoodSamples(CategoryRepository categories, ProductRepository products) {
        Category newArrivals = null;
        Category girls = null;
        Category boys = null;
        for (var category : categories.findAll()) {
            if ("热销推荐".equals(category.getName())) { setCategory(category, "新品到货", "New Arrivals"); newArrivals = categories.save(category); }
            else if ("主食套餐".equals(category.getName())) { setCategory(category, "女童精选", "Girls"); girls = categories.save(category); }
            else if ("饮品".equals(category.getName())) { setCategory(category, "男童精选", "Boys"); boys = categories.save(category); }
        }
        for (var product : products.findAll()) {
            if ("招牌牛肉饭".equals(product.getName())) setProduct(product, newArrivals, "纯棉印花连衣裙", "Cotton Print Dress", "柔软纯棉面料，适合春夏日常穿搭", "Soft cotton dress for everyday spring and summer wear.", new BigDecimal("59.00"));
            else if ("秘制鸡腿饭".equals(product.getName())) setProduct(product, newArrivals, "韩版儿童套装", "Korean Style Kids Set", "上衣与短裤成套搭配，方便批量选款", "Matching top and shorts set for easy bulk selection.", new BigDecimal("79.00"));
            else if ("番茄意面套餐".equals(product.getName())) setProduct(product, girls, "儿童加绒卫衣", "Fleece Kids Sweatshirt", "内里加绒保暖，宽松舒适不束缚", "Warm fleece lining with a relaxed, comfortable fit.", new BigDecimal("49.00"));
            else if ("青柠气泡水".equals(product.getName())) setProduct(product, boys, "宝宝针织开衫", "Baby Knit Cardigan", "亲肤针织，适合叠穿和换季备货", "Soft knit cardigan for layering and seasonal stocking.", new BigDecimal("45.00"));
            else continue;
            products.save(product);
        }
    }

    private void setCategory(Category category, String nameZh, String nameEn) {
        category.setName(nameZh); category.setNameZh(nameZh); category.setNameEn(nameEn);
    }

    private void setProduct(Product product, Category category, String nameZh, String nameEn, String descriptionZh, String descriptionEn, BigDecimal price) {
        if (category != null) product.setCategory(category);
        product.setName(nameZh); product.setNameZh(nameZh); product.setNameEn(nameEn);
        product.setDescription(descriptionZh); product.setDescriptionZh(descriptionZh); product.setDescriptionEn(descriptionEn);
        product.setPrice(price);
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
}
