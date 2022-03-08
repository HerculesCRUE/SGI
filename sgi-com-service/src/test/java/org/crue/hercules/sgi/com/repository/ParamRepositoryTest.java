package org.crue.hercules.sgi.com.repository;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.enums.ParamType;
import org.crue.hercules.sgi.com.model.ContentTpl;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.model.SubjectTpl;
import org.crue.hercules.sgi.com.repository.specification.ParamSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ParamRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ParamRepository repository;

  @Test
  public void findByEmailTplName_ReturnsEmpty() throws Exception {
    // given: no Params for an EmailTpl with with name to find
    String name = "NOT_FOUND_TEMPLATE_NAME";

    // when: find by given name
    List<Param> dataFound = repository.findAll(ParamSpecifications.byEmailTplName(name));

    // then: no Param with given EmailTpl name is found
    Assertions.assertThat(dataFound).isEmpty();
  }

  @Test
  public void findSubjectTplParamByEmailTplName_ReturnsParams() throws Exception {
    // given: tree Params for the EmailTpl with with name to find
    // "EMAIL_01" "SUBJECT_01" "CONTENT_00"
    // "SUBJECT_01" "PARAM_01"
    // "CONTENT_00" "PARAM_02" "PARAM_03"
    populateDB();
    String name = "EMAIL_01";

    // when: find by given name
    List<Param> dataFound = repository.findAll(ParamSpecifications.subjectTplParamByEmailTplName(name));

    // then: expected Params for the given EmailTpl name are found
    // "PARAM_01"
    Assertions.assertThat(dataFound).isNotEmpty();
    Assertions.assertThat(dataFound.size()).isEqualTo(1);
  }

  @Test
  public void findContentTplParamByEmailTplName_ReturnsParams() throws Exception {
    // given: tree Params for the EmailTpl with with name to find
    // "EMAIL_01" "SUBJECT_01" "CONTENT_00"
    // "SUBJECT_01" "PARAM_01"
    // "CONTENT_00" "PARAM_02" "PARAM_03"
    populateDB();
    String name = "EMAIL_01";

    // when: find by given name
    List<Param> dataFound = repository.findAll(ParamSpecifications.contentTplParamByEmailTplName(name));

    // then: expected Params for the given EmailTpl name are found
    // "PARAM_02"
    // "PARAM_03"
    Assertions.assertThat(dataFound).isNotEmpty();
    Assertions.assertThat(dataFound.size()).isEqualTo(2);
  }

  @Test
  public void findByEmailTplName_ReturnsParams() throws Exception {
    // given: tree Params for the EmailTpl with with name to find
    // "EMAIL_01" "SUBJECT_01" "CONTENT_00"
    // "SUBJECT_01" "PARAM_01"
    // "CONTENT_00" "PARAM_02" "PARAM_03"
    populateDB();
    String name = "EMAIL_01";

    // when: find by given name
    List<Param> dataFound = repository.findAll(ParamSpecifications.byEmailTplName(name));

    // then: expected Params for the given EmailTpl name are found
    // "PARAM_01"
    // "PARAM_02"
    // "PARAM_03"
    Assertions.assertThat(dataFound).isNotEmpty();
    Assertions.assertThat(dataFound.size()).isEqualTo(3);
  }

  private void populateDB() {
    // @formatter:off
    List<Param> params = new ArrayList<>();
    for (int paramIdx=0; paramIdx<10; paramIdx++) {
      // "PARAM_00"
      // "PARAM_01"
      // "PARAM_03"
      // "PARAM_04"
      // "PARAM_05"
      // "PARAM_06"
      // "PARAM_07"
      // "PARAM_08"
      // "PARAM_09"
      Param param = Param.builder()
          .name(String.format("PARAM_%02d", paramIdx))
          .type(ParamType.STRING)
          .build();
      params.add(entityManager.persistAndFlush(param));
    }

    List<SubjectTpl> subjectTpls = new ArrayList<>();
    for (int subjectIdx=0, paramIdx=0; subjectIdx<3; subjectIdx++,paramIdx++) {
      // "SUBJECT_00" "PARAM_00"
      // "SUBJECT_01" "PARAM_01"
      // "SUBJECT_02" "PARAM_02"
      SubjectTpl subjectTpl = SubjectTpl.builder()
          .name(String.format("SUBJECT_%02d", subjectIdx))
          .tpl(String.format("Subject with ${PARAM_%02d}", paramIdx))
          .params(params.subList(paramIdx, paramIdx+1))
          .build();
      subjectTpls.add(entityManager.persistAndFlush(subjectTpl));
    }

    List<ContentTpl> contentTpls = new ArrayList<>();
    for (int contentIdx=0, paramIdx=2; contentIdx<2; contentIdx++, paramIdx+=2) {
      // "CONTENT_00" "PARAM_02" "PARAM_03"
      // "CONTENT_01" "PARAM_04" "PARAM_05"
      ContentTpl contentTpl = ContentTpl.builder()
          .name(String.format("CONTENT_%02d", contentIdx))
          .tplText(String.format("Content with ${PARAM_%02d} and ${PARAM_%02d}", paramIdx, paramIdx+1))
          .tplHtml(String.format("<b>Content with ${PARAM_%02d} and ${PARAM_%02d}</b>", paramIdx, paramIdx+1))
          .params(params.subList(paramIdx, paramIdx+2))
          .build();
      contentTpls.add(entityManager.persistAndFlush(contentTpl));
    }

    List<EmailTpl> emailTpls = new ArrayList<>();
    for (int emailIdx=0, subjectIdx=0, contentIdx=1; emailIdx<2; emailIdx++, subjectIdx++, contentIdx--) {
      // "EMAIL_00" "SUBJECT_00" "CONTENT_01"
      // "EMAIL_01" "SUBJECT_01" "CONTENT_00"
      EmailTpl emailTpl = EmailTpl.builder()
          .name(String.format("EMAIL_%02d", emailIdx))
          .subjectTpl(subjectTpls.get(subjectIdx))
          .contentTpl(contentTpls.get(contentIdx))
          .build();
      emailTpls.add(entityManager.persistAndFlush(emailTpl));
    }
    // @formatter:on
  }
}
