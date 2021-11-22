package org.crue.hercules.sgi.framework.core.convert.converter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.data.sort.SortOperation;

import org.springframework.core.convert.converter.Converter;

import lombok.extern.slf4j.Slf4j;

/**
 * A converter converts a source object of type {@code String} to a target of
 * type {@code List<SortCriteria>}.
 */
@Slf4j
public class SortCriteriaConverter implements Converter<String, List<SortCriteria>>, Serializable {
  private static final String MULTIPLE_SORT_SEPARATOR = ";";
  private static final String SORT_SEPARATOR = ",";

  /**
   * Convert the source object of type {@code String} to target type
   * {@code List<SortCriteria>}.
   * 
   * @param source the source object to convert, which must be an instance of
   *               {@code String} (never {@code null})
   * @return the converted object, which must be an instance of
   *         {@code List<SortCriteria>} (potentially {@code null})
   * @throws IllegalArgumentException if the source cannot be converted to the
   *                                  desired target type
   */
  @Override
  public List<SortCriteria> convert(String source) {
    log.debug("convert(String source) - start");
    List<SortCriteria> sortCriterias = new ArrayList<>();
    if (source != null) {
      sortCriterias = Arrays.stream(source.split(MULTIPLE_SORT_SEPARATOR)).map(item -> item.split(SORT_SEPARATOR))
          .map(this::toSortCriteria).filter(Objects::nonNull).collect(Collectors.toList());
    }
    log.debug("convert(String source) - end");
    return sortCriterias;
  }

  private SortCriteria toSortCriteria(String[] parts) {
    log.debug("toSortCriteria(String[] parts) - start");
    if (parts.length != 2) {
      log.debug("toSortCriteria(String[] parts) - end");
      return null;
    }
    SortCriteria sortCriteria = new SortCriteria();
    sortCriteria.setKey(parts[0]);
    sortCriteria.setOperation(SortOperation.fromString(parts[1]));
    log.debug("toSortCriteria(String[] parts) - end");
    return sortCriteria;
  }
}