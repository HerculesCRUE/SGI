package org.crue.hercules.sgi.prc.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProduccionCientificaPredicateResolver implements SgiRSQLPredicateResolver<ProduccionCientifica> {
  private static final String BAD_NUMBER_OF_ARGUMENTS_FOR = "Bad number of arguments for ";
  private static final String FOR = " for ";
  private static final String UNSUPPORTED_OPERATOR = "Unsupported operator: ";

  public enum ComparisonOperatorsAllow {
    EQUAL(new ComparisonOperator("==")),
    IGNORE_CASE_LIKE(new ComparisonOperator("=ik=", "=icase=")),
    GREATER_THAN_OR_EQUAL(new ComparisonOperator("=ge=", ">")),
    LESS_THAN_OR_EQUAL(new ComparisonOperator("=le=", "<="));

    private ComparisonOperator comparisonOperator;

    private ComparisonOperatorsAllow(ComparisonOperator comparisonOperator) {
      this.comparisonOperator = comparisonOperator;
    }

    public ComparisonOperator getComparisonOperator() {
      return comparisonOperator;
    }

    public static ComparisonOperatorsAllow fromComparisonOperator(ComparisonOperator comparisonOperatorSearch) {
      for (ComparisonOperatorsAllow comparisonOperatorAllow : ComparisonOperatorsAllow.values()) {
        if (comparisonOperatorAllow.comparisonOperator.equals(comparisonOperatorSearch)) {
          return comparisonOperatorAllow;
        }
      }
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + comparisonOperatorSearch);
    }
  }

  public enum Property {
    /* Tipo producción */
    TIPO_PRODUCCION("tipoProduccion"),
    /* Fecha publicación */
    FECHA_PUBLICACION("fechaPublicacion"),
    /* Fecha publicación desde */
    FECHA_PUBLICACION_DESDE("fechaPublicacionDesde"),
    /* Fecha publicación hasta */
    FECHA_PUBLICACION_HASTA("fechaPublicacionHasta"),
    /* Título publicación */
    TITULO_PUBLICACION("tituloPublicacion"),
    /* Investigador */
    INVESTIGADOR("investigador"),
    /* Fecha esstado */
    FECHA_ESTADO("fechaEstado"),
    /* ISSB/ISBN */
    ISBN("isbn"),
    /* Grupo investigación */
    GRUPO_INVESTIGACION("grupoInvestigacion");

    private String code;

    private Property(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public static Property fromCode(String code) {
      for (Property property : Property.values()) {
        if (property.code.equals(code)) {
          return property;
        }
      }
      return null;
    }
  }

  private ProduccionCientificaPredicateResolver() {
  }

  public static ProduccionCientificaPredicateResolver getInstance() {
    return new ProduccionCientificaPredicateResolver();
  }

  private Predicate buildByTipoProduccion(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String tipoProduccion = "%" + node.getArguments().get(0) + "%";

    Subquery<String> queryTipoProduccion = buildSubqueryValorCampoProduccionCientifica(cb,
        query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_010_010, operator, tipoProduccion);
    return cb.and(cb.exists(queryTipoProduccion));
  }

  private Predicate buildByTituloPublicacion(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {

      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String tituloPublicacion = "%" + node.getArguments().get(0) + "%";

    Subquery<String> queryTituloPublicacion = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_010_030, operator, tituloPublicacion);
    return cb.and(cb.exists(queryTituloPublicacion));

  }

  private Predicate buildByISBN(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String isbn = "%" + node.getArguments().get(0) + "%";

    Subquery<String> queryISBN = buildSubqueryValorCampoProduccionCientifica(cb, query,
        root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_010_160, operator, isbn);
    return cb.and(cb.exists(queryISBN));
  }

  private Predicate buildByFechaPublicacionDesde(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL))) {

      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaPublicacion = node.getArguments().get(0);

    Subquery<String> queryFechaPublicacion = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_010_140, operator, fechaPublicacion);
    return cb.and(cb.exists(queryFechaPublicacion));
  }

  private Predicate buildByFechaPublicacionHasta(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.LESS_THAN_OR_EQUAL))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaPublicacion = node.getArguments().get(0);

    Subquery<String> queryFechaPublicacion = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_010_140, operator, fechaPublicacion);
    return cb.and(cb.exists(queryFechaPublicacion));
  }

  private Subquery<Long> getSubqueryAutor(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, String personaRef) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    Subquery<Long> queryAutor = cq.subquery(Long.class);
    Root<Autor> subqRoot = queryAutor.from(Autor.class);

    predicatesSubquery
        .add(cb.and(cb.equal(subqRoot.get(Autor_.produccionCientificaId), rootProduccionCientificaId)));
    predicatesSubquery
        .add(cb.and(cb.like(subqRoot.get(Autor_.personaRef), personaRef)));

    queryAutor.select(subqRoot.get(Autor_.id))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryAutor;
  }

  private Predicate buildByInvestigador(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String investigador = "%" + node.getArguments().get(0) + "%";

    Subquery<Long> queryInvestigador = getSubqueryAutor(cb, query, root.get(ProduccionCientifica_.id), investigador);
    return cb.and(cb.exists(queryInvestigador));
  }

  private Predicate buildByFechaEstado(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado, JoinType.LEFT);

    Root<ConfiguracionBaremo> rootConfiguracionBaremo = query.from(ConfiguracionBaremo.class);

    return cb.and(
        cb.greaterThanOrEqualTo(joinEstado.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.epigrafeCVN),
            root.get(ProduccionCientifica_.epigrafeCVN)),
        cb.or(cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.tipoFuente), TipoFuente.CVN),
            cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.tipoFuente), TipoFuente.CVN_OTRO_SISTEMA)),
        cb.or(
            cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.VALIDADO),
            cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.RECHAZADO)));
  }

  private Predicate buildByGrupoInvestigacion(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String grupoRef = node.getArguments().get(0);

    Subquery<Long> queryGrupoInvestigador = getSubqueryAutorGrupo(cb, query, root.get(ProduccionCientifica_.id),
        grupoRef);
    return cb.and(cb.exists(queryGrupoInvestigador));
  }

  private Subquery<Long> getSubqueryAutorGrupo(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, String grupoRef) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    Subquery<Long> queryAutorGrupo = cq.subquery(Long.class);
    Root<AutorGrupo> subqRoot = queryAutorGrupo.from(AutorGrupo.class);

    Join<AutorGrupo, Autor> joinAutor = subqRoot.join(AutorGrupo_.autor);

    predicatesSubquery
        .add(cb.and(cb.equal(joinAutor.get(Autor_.produccionCientificaId), rootProduccionCientificaId)));
    predicatesSubquery
        .add(cb.and(cb.equal(subqRoot.get(AutorGrupo_.grupoRef), grupoRef)));

    queryAutorGrupo.select(subqRoot.get(AutorGrupo_.id))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryAutorGrupo;
  }

  private Subquery<String> buildSubqueryValorCampoProduccionCientifica(CriteriaBuilder cb, CriteriaQuery<?> cq,
      Path<Long> rootProduccionCientificaId, CodigoCVN codigoCVN, ComparisonOperator operator, String valor) {
    List<Predicate> predicatesSubquery = new ArrayList<>();

    ComparisonOperatorsAllow operatorAllow = ComparisonOperatorsAllow.fromComparisonOperator(operator);

    Subquery<String> queryValorCampoProduccionCientifica = cq.subquery(String.class);

    Root<ValorCampo> subqRoot = queryValorCampoProduccionCientifica.from(ValorCampo.class);
    Join<ValorCampo, CampoProduccionCientifica> joinCampoProduccionCientifica = subqRoot.join(
        ValorCampo_.campoProduccionCientifica, JoinType.INNER);
    Join<CampoProduccionCientifica, ProduccionCientifica> joinProduccionCientifica = joinCampoProduccionCientifica.join(
        CampoProduccionCientifica_.produccionCientifica, JoinType.INNER);

    predicatesSubquery
        .add(cb.equal(joinProduccionCientifica.get(ProduccionCientifica_.id), rootProduccionCientificaId));
    predicatesSubquery.add(cb.equal(joinCampoProduccionCientifica.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVN));
    predicatesSubquery.add(cb.equal(joinCampoProduccionCientifica.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVN));

    switch (operatorAllow) {
      case IGNORE_CASE_LIKE:
        predicatesSubquery.add(cb.and(cb.like(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case GREATER_THAN_OR_EQUAL:
        predicatesSubquery.add(cb.and(cb.greaterThanOrEqualTo(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case LESS_THAN_OR_EQUAL:
        predicatesSubquery.add(cb.and(cb.lessThanOrEqualTo(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      case EQUAL:
        predicatesSubquery.add(cb.and(cb.equal(subqRoot.get(ValorCampo_.valor), valor)));
        break;
      default:
    }

    queryValorCampoProduccionCientifica.select(subqRoot.get(ValorCampo_.valor))
        .where(predicatesSubquery.toArray(new Predicate[] {}));

    return queryValorCampoProduccionCientifica;
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (null != property) {
      switch (property) {
        case TIPO_PRODUCCION:
          return buildByTipoProduccion(node, root, query, criteriaBuilder);
        case FECHA_PUBLICACION_DESDE:
          return buildByFechaPublicacionDesde(node, root, query, criteriaBuilder);
        case FECHA_PUBLICACION_HASTA:
          return buildByFechaPublicacionHasta(node, root, query, criteriaBuilder);
        case INVESTIGADOR:
          return buildByInvestigador(node, root, query, criteriaBuilder);
        case TITULO_PUBLICACION:
          return buildByTituloPublicacion(node, root, query, criteriaBuilder);
        case ISBN:
          return buildByISBN(node, root, query, criteriaBuilder);
        case FECHA_ESTADO:
          return buildByFechaEstado(node, root, query, criteriaBuilder);
        case GRUPO_INVESTIGACION:
          return buildByGrupoInvestigacion(node, root, query, criteriaBuilder);
        default:
          return null;
      }
    } else {
      return null;
    }
  }
}
