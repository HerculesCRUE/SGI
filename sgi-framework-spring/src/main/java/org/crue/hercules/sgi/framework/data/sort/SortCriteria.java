package org.crue.hercules.sgi.framework.data.sort;

import lombok.Data;

/**
 * Simple class to hold table sort criterias.
 */
@Data
public class SortCriteria {
  private String key;
  private SortOperation operation;
}