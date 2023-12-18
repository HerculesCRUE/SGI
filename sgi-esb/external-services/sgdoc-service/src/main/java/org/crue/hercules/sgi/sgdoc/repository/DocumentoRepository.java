package org.crue.hercules.sgi.sgdoc.repository;

import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentoRepository extends JpaRepository<Documento, String>, JpaSpecificationExecutor<Documento> {
}
