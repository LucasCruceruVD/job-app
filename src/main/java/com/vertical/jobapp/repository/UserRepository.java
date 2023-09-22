package com.vertical.jobapp.repository;

import com.vertical.jobapp.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneWithAuthoritiesByEmail(String email);
}
