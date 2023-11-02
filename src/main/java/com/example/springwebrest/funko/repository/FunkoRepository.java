package com.example.springwebrest.funko.repository;

import com.example.springwebrest.funko.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoRepository extends JpaRepository<Funko,Long>{
}
