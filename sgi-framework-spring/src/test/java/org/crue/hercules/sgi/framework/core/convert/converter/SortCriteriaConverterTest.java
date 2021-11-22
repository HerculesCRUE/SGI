package org.crue.hercules.sgi.framework.core.convert.converter;

import java.util.List;

import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.data.sort.SortOperation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SortCriteriaConverterTest {
  @InjectMocks
  SortCriteriaConverter sortCriteriaConverter;

  @ParameterizedTest
  @CsvSource(value = {// @formatter:off
      "column; asc; column,asc",
      "column.column; asc; column.column,asc",
      "column; desc; column,desc",
      "column.column; desc; column.column,desc" 
      // @formatter:on
  }, delimiter = ';')
  void convert_sortExpression_returnsSortCriteria(String column, String operator, String query) throws Exception {
    // given: a parametrized expression

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: the right SortCriteria list is returned
    Assertions.assertThat(sortCriterias.size()).isEqualTo(1);
    Assertions.assertThat(sortCriterias.get(0).getKey()).isEqualTo(column);
    Assertions.assertThat(sortCriterias.get(0).getOperation()).isEqualTo(SortOperation.fromString(operator));
  }

  @Test
  void convert_multipleExpresion_returnsSortCriteriaList() throws Exception {
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

  @Test
  void convert_multipleExpresionNestedProperty_returnsSortCriteriaList() throws Exception {
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

  @Test
  void convert_noExpresion_returnsEmptyList() throws Exception {
    // given: a no sort expression
    String query = "value not";

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: no QueryCriteria is returned
    Assertions.assertThat(sortCriterias.size()).isZero();
  }

  @Test
  void convert_null_returnsEmptyList() throws Exception {
    // given: a no sort expression
    String query = null;

    // when: convert method invoqued with given expression
    List<SortCriteria> sortCriterias = sortCriteriaConverter.convert(query);

    // then: no QueryCriteria is returned
    Assertions.assertThat(sortCriterias.size()).isZero();
  }
}