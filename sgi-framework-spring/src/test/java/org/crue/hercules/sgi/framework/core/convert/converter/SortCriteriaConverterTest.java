package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.List;

import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.data.sort.SortOperation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SortCriteriaConverterTest {
  @InjectMocks
  SortCriteriaConverter sortCriteriaConverter;

  /**
   * @throws Exception
   */
  @Test
  public void convertAscendingSortExpression_returnsAscendingSortCriteria() throws Exception {
    // given: an equals expression
    String column = "column";
    String operator = "asc";
    String query = column + "," + operator;

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(1);
    Assertions.assertThat(sortCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(sortCriterias.get(0).getOperation()).isEqualTo(SortOperation.fromString(operator));
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertAscendingSortExpressionNestedProperty_returnsAscendingSortCriteria() throws Exception {
    // given: an equals expression
    String column = "column.column";
    String operator = "asc";
    String query = column + "," + operator;

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(1);
    Assertions.assertThat(sortCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(sortCriterias.get(0).getOperation()).isEqualTo(SortOperation.fromString(operator));
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertDescendingSortExpression_returnsDescendingSortCriteria() throws Exception {
    // given: an equals expression
    String column = "column";
    String operator = "desc";
    String query = column + "," + operator;

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(1);
    Assertions.assertThat(sortCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(sortCriterias.get(0).getOperation()).isEqualTo(SortOperation.fromString(operator));
  }

  /**
   * @throws Exception
   */
  @Test
  public void convertDescendingSortExpressionNestedProperty_returnsDescendingSortCriteria() throws Exception {
    // given: an equals expression
    String column = "column.column";
    String operator = "desc";
    String query = column + "," + operator;

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(1);
    Assertions.assertThat(sortCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(sortCriterias.get(0).getOperation()).isEqualTo(SortOperation.fromString(operator));
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_multipleExpresion_returnsSortCriteriaList() throws Exception {
    // given: an equals expression
    String column = "column";
    String query = "";
    int elements = 3;
    for (int i = 0; i < elements; i++) {
      query += ";" + column + i + "," + (i % 2 == 0 ? "asc" : "desc");
    }
    query = query.substring(1);

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(elements);
    for (int i = 0; i < elements; i++) {
      Assertions.assertThat(sortCriterias.get(i).getKey()).isEqualTo(column + i);
      Assertions.assertThat(sortCriterias.get(i).getOperation())
          .isEqualTo(SortOperation.fromString((i % 2 == 0 ? "asc" : "desc")));
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_multipleExpresionNestedProperty_returnsSortCriteriaList() throws Exception {
    // given: an equals expression
    String column = "column.column";
    String query = "";
    int elements = 3;
    for (int i = 0; i < elements; i++) {
      query += ";" + column + i + "," + (i % 2 == 0 ? "asc" : "desc");
    }
    query = query.substring(1);

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(elements);
    for (int i = 0; i < elements; i++) {
      Assertions.assertThat(sortCriterias.get(i).getKey()).isEqualTo(column + i);
      Assertions.assertThat(sortCriterias.get(i).getOperation())
          .isEqualTo(SortOperation.fromString((i % 2 == 0 ? "asc" : "desc")));
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void convert_noExpresion_returnsEmptyList() throws Exception {
    // given: a no sort expression
    String query = "value not";

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: no QueryCriteria is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(0);
  }
}