package org.crue.hercules.sgi.eti.repository.predicate;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.Respuesta_;
import org.crue.hercules.sgi.eti.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class MemoriaPredicateResolver implements SgiRSQLPredicateResolver<Memoria> {
  private static final String PERCENT_SIGN = "%";

  private enum Property {
    /* Contenido del texto de respuesta del formulario de memoria */
    TEXTO_CONTENIDO_RESPUESTA_FORMULARIO("textoContenidoRespuestaFormulario");

    private String code;

    private Property(String code) {
      this.code = code;
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

  public static MemoriaPredicateResolver getInstance() {
    return new MemoriaPredicateResolver();
  }

  private Predicate buildByTextoRespuesta(ComparisonNode node, Root<Memoria> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);
    CriteriaQuery<MemoriaPeticionEvaluacion> cq = cb.createQuery(MemoriaPeticionEvaluacion.class);
    String texto = node.getArguments().get(0);

    Predicate predicateTextoFormulario = cb.in(root.get(Memoria_.id))
        .value(getIdsMemoriaRespuesta(cb, cq, texto));

    return predicateTextoFormulario;
  }

  private Subquery<Long> getIdsMemoriaRespuesta(CriteriaBuilder cb,
      CriteriaQuery<MemoriaPeticionEvaluacion> cq, String respuesta) {

    Subquery<Long> queryGetIdMemoria = cq.subquery(Long.class);
    Root<Respuesta> subqRoot = queryGetIdMemoria.from(Respuesta.class);

    List<Predicate> predicates = new ArrayList<>();

    Expression<String> expression = subqRoot.get(Respuesta_.valor);
    expression = cb.function("remove_accents", String.class, expression);
    expression = cb.function("remove_html_tags", String.class, expression);
    expression = cb.function("search_in_value_of_json", String.class, expression);

    String respuestaSinTildes = removeAccentsAndSpecialChars(respuesta);
    predicates.add(cb.like(
        cb.upper(expression),
        PERCENT_SIGN + respuestaSinTildes.toUpperCase() + PERCENT_SIGN));

    queryGetIdMemoria.select(subqRoot.get(Respuesta_.memoria).get(Memoria_.id))
        .where(predicates.toArray(new Predicate[] {}));

    return queryGetIdMemoria;
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Memoria> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());

    if (property == null) {
      return null;
    }

    switch (property) {
      case TEXTO_CONTENIDO_RESPUESTA_FORMULARIO:
        return buildByTextoRespuesta(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

  private String removeAccentsAndSpecialChars(String input) {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("[^\\p{ASCII}]", "")
        .replaceAll("[^a-zA-Z0-9 ]", "")
        .replaceAll("\\s+", " ")
        .trim();
  }
}
