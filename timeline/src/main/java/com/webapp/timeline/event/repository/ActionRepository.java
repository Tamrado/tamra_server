package com.webapp.timeline.event.repository;

import com.webapp.timeline.event.domain.AbstractAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<AbstractAction, Long> {
}
