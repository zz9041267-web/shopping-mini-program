package kun.cn.repository;

import kun.cn.domain.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    List<CustomerAddress> findByCustomerNameOrderByDefaultAddressDescIdDesc(String customerName);
    Optional<CustomerAddress> findByIdAndCustomerName(Long id, String customerName);
    List<CustomerAddress> findByCustomerNameAndDefaultAddressTrue(String customerName);
}
