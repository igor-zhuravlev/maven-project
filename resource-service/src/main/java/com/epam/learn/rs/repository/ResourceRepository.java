package com.epam.learn.rs.repository;

import com.epam.learn.rs.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Integer> { }
