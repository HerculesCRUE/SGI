package org.crue.hercules.sgi.framework.data.sort;

import lombok.Data;

@Data
public class SortCriteria {
  private String key;
  private SortOperation operation;
}