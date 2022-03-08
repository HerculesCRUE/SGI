package org.crue.hercules.sgi.com.repository;

import java.util.List;

import org.crue.hercules.sgi.com.model.EmailParam;
import org.crue.hercules.sgi.com.model.EmailParamPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmailParam}.
 */
@Repository
public interface EmailParamRepository
    extends JpaRepository<EmailParam, EmailParamPK>, JpaSpecificationExecutor<EmailParam> {

  List<EmailParam> findByPkEmailId(Long id);

  Long deleteByPkEmailId(Long id);
}
