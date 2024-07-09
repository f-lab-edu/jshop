package jshop.domain.address.repository;

import java.util.List;
import java.util.Optional;
import jshop.domain.address.entity.Address;
import jshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findById(Long id);

    List<Address> findByUser(User user);
}
