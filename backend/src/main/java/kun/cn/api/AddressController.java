package kun.cn.api;

import kun.cn.domain.CustomerAddress;
import kun.cn.repository.CustomerAddressRepository;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin
public class AddressController {
    private final CustomerAddressRepository addresses;
    public AddressController(CustomerAddressRepository addresses) { this.addresses = addresses; }

    @GetMapping public List<CustomerAddress> mine(Authentication auth) { return addresses.findByCustomerNameOrderByDefaultAddressDescIdDesc(auth.getName()); }

    @PostMapping @Transactional
    public ResponseEntity<?> create(@RequestBody AddressRequest request, Authentication auth) {
        if (!valid(request)) return invalid();
        var address = new CustomerAddress(); copy(address, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(addresses.save(address));
    }

    @PutMapping("/{id}") @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AddressRequest request, Authentication auth) {
        if (!valid(request)) return invalid();
        var address = addresses.findByIdAndCustomerName(id, auth.getName()).orElse(null);
        if (address == null) return ResponseEntity.notFound().build();
        copy(address, request, auth.getName()); return ResponseEntity.ok(addresses.save(address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        var address = addresses.findByIdAndCustomerName(id, auth.getName()).orElse(null);
        if (address == null) return ResponseEntity.notFound().build();
        addresses.delete(address); return ResponseEntity.noContent().build();
    }

    private boolean valid(AddressRequest request) { return request != null && !blank(request.recipientName()) && !blank(request.contactPhone()) && !blank(request.shippingAddress()); }
    private void copy(CustomerAddress address, AddressRequest request, String customerName) {
        if (request.defaultAddress()) addresses.findByCustomerNameAndDefaultAddressTrue(customerName).forEach(item -> item.setDefaultAddress(false));
        address.setCustomerName(customerName); address.setRecipientName(request.recipientName().trim()); address.setContactPhone(request.contactPhone().trim()); address.setShippingAddress(request.shippingAddress().trim()); address.setDefaultAddress(request.defaultAddress());
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private ResponseEntity<Map<String, String>> invalid() { return ResponseEntity.badRequest().body(Map.of("message", "请完整填写收货信息")); }
    public record AddressRequest(String recipientName, String contactPhone, String shippingAddress, boolean defaultAddress) { }
}
