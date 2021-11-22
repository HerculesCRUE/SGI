package org.crue.hercules.sgi.framework.data.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SortOperationTest {
  @Test
  void fromString_asc_returnsSortOperationASC() {
    assertEquals(SortOperation.ASC, SortOperation.fromString("asc"));
  }

  @Test
  void fromString_desc_returnsSortOperationDESC() {
    assertEquals(SortOperation.DESC, SortOperation.fromString("desc"));
  }

  @Test
  void fromString_other_returnsNull() {
    assertNull(SortOperation.fromString("other"));
  }
}
