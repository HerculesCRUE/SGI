package org.crue.hercules.sgi.pii.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base Activable Entity.
 */
@NoArgsConstructor
@SuperBuilder
public abstract class BaseActivableEntity extends Activable implements Identifiable, Serializable {
}
