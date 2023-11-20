package com.example.springwebrest.rest.funko.repository;

import com.example.springwebrest.rest.funko.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoRepository extends JpaRepository<Funko,Long>, JpaSpecificationExecutor<Funko> {
}
