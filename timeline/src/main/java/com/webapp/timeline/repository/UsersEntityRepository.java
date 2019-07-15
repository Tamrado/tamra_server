package com.webapp.timeline.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository <SampleEntity, Long> {
    
}

