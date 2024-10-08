package com.keviny.springmvc.shoprecord.repository;

import com.keviny.springmvc.shoprecord.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

}
