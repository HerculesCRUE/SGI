package org.crue.hercules.sgi.framework.data.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Table sort operation.
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public enum SortOperation {
  /** Ascending sort opertaion */
  ASC("asc"),
  /** Descending sort operation */
  DESC("desc");

  private final String asString;

  /**
   * Gets the {@link SortOperation} from the input string
   * 
   * @param input the textual sort operation
   * @return eht {@link SortOperation}
   */
  public static SortOperation fromString(String input) {
    log.debug("fromString(String input) - start");
    for (SortOperation operation : SortOperation.values()) {
      if (operation.asString.equalsIgnoreCase(input)) {
        log.debug("fromString(String input) - end");
        return operation;
      }
    }
    log.warn("No valid sort operation found");
    log.debug("fromString(String input) - end");
    return null;
  }
}