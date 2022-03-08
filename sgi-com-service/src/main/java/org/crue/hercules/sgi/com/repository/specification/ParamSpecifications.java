package org.crue.hercules.sgi.com.repository.specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;

import org.crue.hercules.sgi.com.model.ContentTpl;
import org.crue.hercules.sgi.com.model.ContentTpl_;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.EmailTpl_;
import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.model.Param_;
import org.crue.hercules.sgi.com.model.SubjectTpl;
import org.crue.hercules.sgi.com.model.SubjectTpl_;
import org.springframework.data.jpa.domain.Specification;

public class ParamSpecifications {
  /**
   * Utility class, can't be instantiated.
   */
  private ParamSpecifications() {
    throw new IllegalStateException("Utility class");
  }


  public static Specification<Param> subjectTplParamByEmailTplName(String name) {
    return (root, query, cb) -> {
      ListJoin<Param, SubjectTpl> joinSubjectTpl = root.join(Param_.subjectTpls, JoinType.LEFT);
      ListJoin<SubjectTpl, EmailTpl> joinSubjectTplEmailTpl = joinSubjectTpl.join(SubjectTpl_.emailTpls, JoinType.LEFT);

      return cb.equal(joinSubjectTplEmailTpl.get(EmailTpl_.name), name);
    };
  }

  public static Specification<Param> contentTplParamByEmailTplName(String name) {
    return (root, query, cb) -> {
      ListJoin<Param, ContentTpl> joinContentTpl = root.join(Param_.contentTpls, JoinType.LEFT);
      ListJoin<ContentTpl, EmailTpl> joinContentTplEmailTpl = joinContentTpl.join(ContentTpl_.emailTpls, JoinType.LEFT);

      return cb.equal(joinContentTplEmailTpl.get(EmailTpl_.name), name);
    };
  }

  public static Specification<Param> byEmailTplName(String name) {
    return Specification.where(subjectTplParamByEmailTplName(name)).or(contentTplParamByEmailTplName(name));
  }

}