package kun.cn.api;

import kun.cn.domain.*;
import kun.cn.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {
    private final OrderRepository orders;
    private final ProductRepository products;
    private final CustomerAddressRepository addresses;

    public OrderController(OrderRepository o, ProductRepository p, CustomerAddressRepository a) {
        orders = o;
        products = p;
        addresses = a;
    }

    @GetMapping
    public List<ShopOrder> mine(Authentication auth) {
        return orders.findByCustomerNameOrderByCreatedAtDesc(auth.getName());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody CreateOrder request, Authentication auth) {
        if (request.items() == null || request.items().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "购物车为空"));
        var address = request.addressId() == null ? null : addresses.findByIdAndCustomerName(request.addressId(), auth.getName()).orElse(null);
        if (address == null) return ResponseEntity.badRequest().body(Map.of("message", "Shipping address is required"));
        var order = new ShopOrder();
        order.setOrderNo("OD" + System.currentTimeMillis());
        order.setCustomerName(auth.getName());
        order.setRecipientName(address.getRecipientName());
        order.setContactPhone(address.getContactPhone());
        order.setShippingAddress(address.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;
        for (var line : request.items()) {
            if (line.productId() == null || line.quantity() == null || line.quantity() < 1 || blank(line.sizeCode()) || blank(line.colorCode()))
                return ResponseEntity.badRequest().body(Map.of("message", "请选择尺码、颜色并填写数量"));
            var product = products.findById(line.productId()).orElse(null);
            if (product == null || !Boolean.TRUE.equals(product.getOnSale()))
                return ResponseEntity.badRequest().body(Map.of("message", "商品已下架"));
            var size = product.getSizeSet() == null ? null : product.getSizeSet().getOptions().stream().filter(option -> line.sizeCode().equals(option.getCode())).findFirst().orElse(null);
            var color = product.getColorSet() == null ? null : product.getColorSet().getOptions().stream().filter(option -> line.colorCode().equals(option.getCode())).findFirst().orElse(null);
            if (size == null || color == null)
                return ResponseEntity.badRequest().body(Map.of("message", "所选尺码或颜色已失效，请重新选择"));
            if (product.getStock() < line.quantity())
                return ResponseEntity.badRequest().body(Map.of("message", product.getName() + " 库存不足"));
            product.setStock(product.getStock() - line.quantity());
            var item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getNameZh() == null ? product.getName() : product.getNameZh());
            item.setProductNameEn(product.getNameEn());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(line.quantity());
            item.setSizeCode(size.getCode());
            item.setSizeLength(size.getLengthValue());
            item.setSizeWidth(size.getWidthValue());
            item.setColorCode(color.getCode());
            item.setColorNameZh(color.getNameZh());
            item.setColorNameEn(color.getNameEn());
            item.setColorHexValue(color.getHexValue());
            order.getItems().add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
        }
        order.setTotalAmount(total);
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.save(order));
    }

    public record CreateOrder(List<Line> items, Long addressId) {
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }

    public record Line(Long productId, Integer quantity, String sizeCode, String colorCode) {
    }
}
