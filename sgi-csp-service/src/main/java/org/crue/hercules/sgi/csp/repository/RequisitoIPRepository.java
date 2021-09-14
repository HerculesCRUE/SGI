package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPRepository extends JpaRepository<RequisitoIP, Long>, JpaSpecificationExecutor<RequisitoIP> {
}
