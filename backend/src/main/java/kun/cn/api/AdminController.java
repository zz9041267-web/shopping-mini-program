package kun.cn.api;

import kun.cn.domain.*;
import kun.cn.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final ShopProfileRepository shops;
    private final SizeSetRepository sizeSets;
    private final ColorSetRepository colorSets;

    public AdminController(CategoryRepository c, ProductRepository p, OrderRepository o, ShopProfileRepository s, SizeSetRepository sizeSetRepository, ColorSetRepository colorSetRepository) {
        categories = c; products = p; orders = o; shops = s; sizeSets = sizeSetRepository; colorSets = colorSetRepository;
    }

    @GetMapping("/shop")
    public ShopProfile shop() { return shops.findById(1L).orElseGet(() -> shops.save(new ShopProfile())); }

    @PutMapping("/shop")
    public ResponseEntity<?> updateShop(@RequestBody ShopRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn())) return invalid("请填写店铺中英文名称");
        if (!validImage(request.coverImageBase64()) || !validImage(request.wechatQrBase64())) return invalid("图片格式不正确或文件过大");
        var shop = shops.findById(1L).orElseGet(ShopProfile::new);
        shop.setNameZh(request.nameZh()); shop.setNameEn(request.nameEn());
        shop.setNoticeZh(request.noticeZh()); shop.setNoticeEn(request.noticeEn());
        shop.setName(request.nameZh()); shop.setNotice(request.noticeZh());
        shop.setCoverImageBase64(request.coverImageBase64()); shop.setWechatQrBase64(request.wechatQrBase64());
        return ResponseEntity.ok(shops.save(shop));
    }

    @GetMapping("/categories")
    public List<Category> categories() { return categories.findAllByOrderBySortOrderAsc(); }

    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn())) return invalid("请填写分类中英文名称");
        var category = new Category(request.nameZh().trim(), request.nameEn().trim(), request.sortOrder() == null ? 0 : request.sortOrder());
        return ResponseEntity.status(HttpStatus.CREATED).body(categories.save(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn())) return invalid("请填写分类中英文名称");
        var category = categories.findById(id).orElse(null);
        if (category == null) return ResponseEntity.notFound().build();
        category.setNameZh(request.nameZh().trim()); category.setNameEn(request.nameEn().trim()); category.setName(request.nameZh().trim());
        if (request.sortOrder() != null) category.setSortOrder(request.sortOrder());
        return ResponseEntity.ok(categories.save(category));
    }

    @GetMapping("/products")
    public List<Product> products() { return products.findAllByOrderByIdDesc(); }

    @GetMapping("/size-sets")
    public List<SizeSet> sizeSets() { return sizeSets.findAll(); }

    @PostMapping("/size-sets")
    public ResponseEntity<?> addSizeSet(@RequestBody SizeSetRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn()) || !validSizeOptions(request.options())) return invalid("尺码组信息不完整");
        var set = new SizeSet();
        set.setNameZh(request.nameZh().trim()); set.setNameEn(request.nameEn().trim()); set.setOptions(toSizeOptions(request.options()));
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeSets.save(set));
    }

    @PutMapping("/size-sets/{id}")
    public ResponseEntity<?> updateSizeSet(@PathVariable Long id, @RequestBody SizeSetRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn()) || !validSizeOptions(request.options())) return invalid("尺码组信息不完整");
        var set = sizeSets.findById(id).orElse(null);
        if (set == null) return ResponseEntity.notFound().build();
        set.setNameZh(request.nameZh().trim()); set.setNameEn(request.nameEn().trim()); set.setOptions(toSizeOptions(request.options()));
        return ResponseEntity.ok(sizeSets.save(set));
    }

    @GetMapping("/color-sets")
    public List<ColorSet> colorSets() { return colorSets.findAll(); }

    @PostMapping("/color-sets")
    public ResponseEntity<?> addColorSet(@RequestBody ColorSetRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn()) || !validColorOptions(request.options())) return invalid("颜色组信息不完整");
        var set = new ColorSet();
        set.setNameZh(request.nameZh().trim()); set.setNameEn(request.nameEn().trim()); set.setOptions(toColorOptions(request.options()));
        return ResponseEntity.status(HttpStatus.CREATED).body(colorSets.save(set));
    }

    @PutMapping("/color-sets/{id}")
    public ResponseEntity<?> updateColorSet(@PathVariable Long id, @RequestBody ColorSetRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn()) || !validColorOptions(request.options())) return invalid("颜色组信息不完整");
        var set = colorSets.findById(id).orElse(null);
        if (set == null) return ResponseEntity.notFound().build();
        set.setNameZh(request.nameZh().trim()); set.setNameEn(request.nameEn().trim()); set.setOptions(toColorOptions(request.options()));
        return ResponseEntity.ok(colorSets.save(set));
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn())) return invalid("请填写商品中英文名称");
        if (request.price() == null || request.price().signum() < 0 || request.stock() == null || request.stock() < 0) return invalid("售价或库存不正确");
        if (request.categoryId() == null || request.sizeSetId() == null || request.colorSetId() == null) return invalid("请选择商品分类、尺码组和颜色组");
        if (!validImages(request.displayImagesBase64()) || !validImages(request.detailImagesBase64())) return invalid("图片格式不正确、数量过多或文件过大");
        var category = categories.findById(request.categoryId()).orElse(null);
        var sizeSet = sizeSets.findById(request.sizeSetId()).orElse(null);
        var colorSet = colorSets.findById(request.colorSetId()).orElse(null);
        if (category == null || sizeSet == null || colorSet == null) return invalid("商品分类、尺码组或颜色组不存在");
        var product = new Product();
        product.setNameZh(request.nameZh().trim()); product.setNameEn(request.nameEn().trim());
        product.setDescriptionZh(orEmpty(request.descriptionZh())); product.setDescriptionEn(orEmpty(request.descriptionEn()));
        product.setName(product.getNameZh()); product.setDescription(product.getDescriptionZh());
        product.setPrice(request.price()); applyImages(product, request); product.setStock(request.stock()); product.setOnSale(true); product.setCategory(category); product.setSizeSet(sizeSet); product.setColorSet(colorSet);
        return ResponseEntity.status(HttpStatus.CREATED).body(products.save(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        if (blank(request.nameZh()) || blank(request.nameEn())) return invalid("请填写商品中英文名称");
        if (request.price() == null || request.price().signum() < 0 || request.stock() == null || request.stock() < 0) return invalid("售价或库存不正确");
        if (request.categoryId() == null || request.sizeSetId() == null || request.colorSetId() == null) return invalid("请选择商品分类、尺码组和颜色组");
        if (!validImages(request.displayImagesBase64()) || !validImages(request.detailImagesBase64())) return invalid("图片格式不正确、数量过多或文件过大");
        var product = products.findById(id).orElse(null);
        var category = categories.findById(request.categoryId()).orElse(null);
        var sizeSet = sizeSets.findById(request.sizeSetId()).orElse(null);
        var colorSet = colorSets.findById(request.colorSetId()).orElse(null);
        if (product == null || category == null || sizeSet == null || colorSet == null) return ResponseEntity.notFound().build();
        product.setNameZh(request.nameZh().trim()); product.setNameEn(request.nameEn().trim());
        product.setDescriptionZh(orEmpty(request.descriptionZh())); product.setDescriptionEn(orEmpty(request.descriptionEn()));
        product.setName(product.getNameZh()); product.setDescription(product.getDescriptionZh());
        product.setPrice(request.price()); applyImages(product, request); product.setStock(request.stock()); product.setCategory(category); product.setSizeSet(sizeSet); product.setColorSet(colorSet);
        return ResponseEntity.ok(products.save(product));
    }

    @PatchMapping("/products/{id}/sale")
    public ResponseEntity<?> sale(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        var product = products.findById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();
        product.setOnSale(body.getOrDefault("onSale", true));
        return ResponseEntity.ok(products.save(product));
    }

    @GetMapping("/orders")
    public List<ShopOrder> orders(@RequestParam(required = false) String orderNo) { return blank(orderNo) ? orders.findAllByOrderByCreatedAtDesc() : orders.findByOrderNoContainingIgnoreCaseOrderByCreatedAtDesc(orderNo.trim()); }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var order = orders.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        try { order.setStatus(ShopOrder.Status.valueOf(body.get("status"))); return ResponseEntity.ok(orders.save(order)); }
        catch (Exception e) { return invalid("无效订单状态"); }
    }

    private ResponseEntity<Map<String, String>> invalid(String message) { return ResponseEntity.badRequest().body(Map.of("message", message)); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String orEmpty(String value) { return value == null ? "" : value.trim(); }
    private boolean validImage(String image) { return image == null || image.isBlank() || (image.startsWith("data:image/") && image.length() <= 4_200_000); }
    private boolean validImages(List<String> images) { return images == null || (images.size() <= 5 && images.stream().allMatch(this::validImage)); }
    private boolean validSizeOptions(List<SizeOptionRequest> options) { return options != null && !options.isEmpty() && options.stream().allMatch(item -> item != null && !blank(item.code()) && item.lengthValue() != null && item.lengthValue() >= 0 && item.widthValue() != null && item.widthValue() >= 0); }
    private boolean validColorOptions(List<ColorOptionRequest> options) { return options != null && !options.isEmpty() && options.stream().allMatch(item -> item != null && !blank(item.code()) && !blank(item.nameZh()) && !blank(item.nameEn()) && item.hexValue() != null && item.hexValue().matches("^#[0-9a-fA-F]{6}$")); }
    private List<SizeOption> toSizeOptions(List<SizeOptionRequest> options) { return options.stream().map(item -> new SizeOption(item.code().trim(), item.lengthValue(), item.widthValue())).toList(); }
    private List<ColorOption> toColorOptions(List<ColorOptionRequest> options) { return options.stream().map(item -> new ColorOption(item.code().trim(), item.nameZh().trim(), item.nameEn().trim(), item.hexValue())).toList(); }
    private void applyImages(Product product, ProductRequest request) {
        var displayImages = request.displayImagesBase64() == null ? new ArrayList<String>() : request.displayImagesBase64();
        if (displayImages.isEmpty() && !blank(request.imageBase64())) displayImages = List.of(request.imageBase64());
        product.setDisplayImagesBase64(displayImages);
        product.setDetailImagesBase64(request.detailImagesBase64());
        product.setImageBase64(displayImages.isEmpty() ? null : displayImages.get(0));
    }

    public record ShopRequest(String nameZh, String nameEn, String noticeZh, String noticeEn, String coverImageBase64, String wechatQrBase64) {}
    public record CategoryRequest(String nameZh, String nameEn, Integer sortOrder) {}
    public record SizeOptionRequest(String code, Integer lengthValue, Integer widthValue) {}
    public record SizeSetRequest(String nameZh, String nameEn, List<SizeOptionRequest> options) {}
    public record ColorOptionRequest(String code, String nameZh, String nameEn, String hexValue) {}
    public record ColorSetRequest(String nameZh, String nameEn, List<ColorOptionRequest> options) {}
    public record ProductRequest(String nameZh, String nameEn, String descriptionZh, String descriptionEn, BigDecimal price, String imageBase64, List<String> displayImagesBase64, List<String> detailImagesBase64, Integer stock, Long categoryId, Long sizeSetId, Long colorSetId) {}
}
