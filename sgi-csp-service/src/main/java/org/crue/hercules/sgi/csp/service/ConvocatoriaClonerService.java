package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPartidaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConvocatoriaClonerService {

  private final ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  private final ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;
  private final ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  private final ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  private final ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  private final RequisitoIPRepository requisitoIPRepository;
  private final RequisitoIPNivelAcademicoRepository requisitoIPNivelAcademicoRepository;
  private final RequisitoIPCategoriaProfesionalRepository requisitoIPCategoriaProfesionalRepository;
  private final RequisitoEquipoRepository requisitoEquipoRepository;
  private final RequisitoEquipoNivelAcademicoRepository requisitoEquipoNivelAcademicoRepository;
  private final RequisitoEquipoCategoriaProfesionalRepository requisitoEquipoCategoriaProfesionalRepository;
  private final ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  private final ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  private final ConvocatoriaPartidaRepository convocatoriaPartidaRepository;

  /**
   * Se copian todos los campos de la tabla "Convocatoria" de la convocatoria
   * seleccionada. Título (campo "titulo") Unidad de gestión (campo
   * "unidadGestionRef") Modelo de ejecución (campo "modeloEjecucion") Tipo
   * finalidad (campo "finalidad") Identificación (campo "codigo") Entidad gestora
   * ( se crea el registro en la tabla "ConvocatoriaEntidadGestora" con el campo
   * "convocatoria" la nueva convocatoria creada y "empresaRef" el valor de la
   * empresa en la convocatoria que se esta clonando) Fecha publicación (campo
   * "fechaPublicacion") Fecha provisional (campo "fechaProvisiona") Fecha
   * concesión (campo "fechaConcesion") Duración de la actividad resultante (campo
   * "duracion") Ámbito geográfico (campo "ambitoGeografico") Régimen concurrencia
   * (campo "regimenConcurrencia") Clasificación CVN/Producción científica (campo
   * "clasificacionCVN") Objeto o descripción general de la convocatoria (campo
   * "objeto") Observaciones (campo "observaciones") Estado ( se pone el estado
   * "Borrador") Activo (se pone a true)
   * 
   * @param toClone la convocatoria de la que hacer el clon
   * @return la convocatoria clonada
   */
  public Convocatoria cloneBasicConvocatoriaData(Convocatoria toClone) {

    return Convocatoria.builder().activo(Boolean.TRUE).titulo(toClone.getTitulo())
        .unidadGestionRef(toClone.getUnidadGestionRef()).modeloEjecucion(toClone.getModeloEjecucion())
        .finalidad(toClone.getFinalidad()).codigo(toClone.getCodigo()).fechaConcesion(toClone.getFechaConcesion())
        .fechaProvisional(toClone.getFechaProvisional()).fechaPublicacion(toClone.getFechaPublicacion())
        .duracion(toClone.getDuracion()).excelencia(toClone.getExcelencia())
        .ambitoGeografico(toClone.getAmbitoGeografico())
        .formularioSolicitud(toClone.getFormularioSolicitud()).regimenConcurrencia(toClone.getRegimenConcurrencia())
        .clasificacionCVN(toClone.getClasificacionCVN()).objeto(toClone.getObjeto())
        .observaciones(toClone.getObservaciones()).estado(Convocatoria.Estado.BORRADOR).build();
  }

  /**
   * Obtine los objetos {@link ConvocatoriaAreaTematica} asociados a la
   * convocatoria que se está clonando y las copia a la {@link Convocatoria}
   * clonada.
   *
   * @param convocatoriaId id de la {@link Convocatoria} a clonar
   * @param cloned         {@link Convocatoria} clonada
   */
  public void cloneConvocatoriaAreasTematicas(Long convocatoriaId, Convocatoria cloned) {
    Optional<ConvocatoriaAreaTematica> area = convocatoriaAreaTematicaRepository.findByConvocatoriaId(convocatoriaId);

    if (!area.isPresent()) {
      return;
    }
    this.convocatoriaAreaTematicaRepository
        .save(ConvocatoriaAreaTematica.builder().areaTematica(area.get().getAreaTematica())
            .convocatoriaId(cloned.getId()).observaciones(cloned.getObservaciones()).build());
  }

  /**
   * Genera un objeto de tipo {@link ConvocatoriaEntidadGestora} y lo persiste en
   * la base de datos, se asocia a la convocatoria clonada, la propiedad
   * entidadRef es de la convocatoria que se está clonando
   *
   * @param convocatoriaToCloneId id de la convocatoria que se está clonando
   * @param convocatoriaClonedId  convocatoria clonada
   */
  public void cloneEntidadesGestoras(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    List<ConvocatoriaEntidadGestora> entidadesGestoras = this.convocatoriaEntidadGestoraRepository
        .findAllByConvocatoriaId(convocatoriaToCloneId);

    if (!CollectionUtils.isEmpty(entidadesGestoras)) {

      this.convocatoriaEntidadGestoraRepository.save(ConvocatoriaEntidadGestora.builder()
          .convocatoriaId(convocatoriaClonedId).entidadRef(entidadesGestoras.get(0).getEntidadRef()).build());
    }
  }

  /**
   * Clona todos los objetos {@link ConvocatoriaEntidadConvocante} que se
   * encuentran asociados a la convocatoria a clonar, manteniendo la entidadRef y
   * el programa.
   * 
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneConvocatoriasEntidadesConvocantes(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    this.convocatoriaEntidadConvocanteRepository.findByProgramaIsNotNullAndConvocatoriaId(convocatoriaToCloneId)
        .stream().forEach(entidad -> this.convocatoriaEntidadConvocanteRepository
            .save(ConvocatoriaEntidadConvocante.builder().convocatoriaId(convocatoriaClonedId)
                .entidadRef(entidad.getEntidadRef()).programa(entidad.getPrograma()).build()));
  }

  /**
   * Clona todos los objetos {@link ConvocatoriaEntidadFinanciadora} que se
   * encuentran asociados a la convocatoria a clonar, Se copian todas las
   * entidades financiadoras con los mismos datos de fuente y tipo de
   * financiación, ámbito geográfico, importe y porcentaje de financiación. Se
   * crean tantos registros en la tabla "ConvocatoriaEntidadFinanciadora" como
   * tenga la convocatoria que se esta clonando y con los mismos datos en los
   * campos "empresaRef", "fuenteFinanciacion", "tipoFinanciacion",
   * "porcentajeFinanciacion" y "importeFinanciacion", la convocatoria será el
   * identificador de la convocatoria que se esta creando
   * 
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneConvocatoriasEntidadesFinanciadoras(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    this.convocatoriaEntidadFinanciadoraRepository.findByConvocatoriaId(convocatoriaToCloneId).stream()
        .forEach(
            entidad -> this.convocatoriaEntidadFinanciadoraRepository.save(ConvocatoriaEntidadFinanciadora.builder()
                .convocatoriaId(convocatoriaClonedId).entidadRef(entidad.getEntidadRef())
                .fuenteFinanciacion(entidad.getFuenteFinanciacion()).tipoFinanciacion(entidad.getTipoFinanciacion())
                .porcentajeFinanciacion(entidad.getPorcentajeFinanciacion())
                .importeFinanciacion(entidad.getImporteFinanciacion()).build()));
  }

  /**
   * Se copian todos los periodos con los mismos datos (mes inicial, mes final,
   * fecha inicio presentación, fecha fin presentación, tipo y observaciones). Se
   * crean tantos registros en la tabla "ConvocatoriaPeriodoJustificacion" como
   * tenga la convocatoria que se esta clonando y con los mismos datos en los
   * campos "numPeriodo", "mesInicial", "mesFinal", "fechaInicioPresentacion",
   * "fechaFinPresentacion", "tipoJustificacion" y "observaciones", la
   * convocatoria será el identificador de la convocatoria que se esta creando
   * 
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void clonePeriodosJustificacion(Long convocatoriaToCloneId, Long convocatoriaClonedId) {
    this.convocatoriaPeriodoJustificacionRepository.findAllByConvocatoriaId(convocatoriaToCloneId).stream()
        .forEach(
            periodo -> this.convocatoriaPeriodoJustificacionRepository.save(ConvocatoriaPeriodoJustificacion.builder()
                .convocatoriaId(convocatoriaClonedId).fechaFinPresentacion(periodo.getFechaFinPresentacion())
                .numPeriodo(periodo.getNumPeriodo()).fechaInicioPresentacion(periodo.getFechaInicioPresentacion())
                .mesFinal(periodo.getMesFinal()).mesInicial(periodo.getMesInicial())
                .observaciones(periodo.getObservaciones()).tipo(periodo.getTipo()).build()));
  }

  /**
   * Se crean tantos registros de tipo {@link ConvocatoriaPeriodoJustificacion}
   * como tenga la {@link Convocatoria} que se esta clonando y con los mismos
   * datos en los campos "numPeriodo", "mesInicial", "mesFinal",
   * "fechaInicioPresentacion", "fechaFinPresentacion", "tipoSeguimiento" y
   * "observaciones", la convocatoria será el identificador de la convocatoria que
   * se esta creando
   * 
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneConvocatoriaPeriodosSeguimientoCientifico(Long convocatoriaToCloneId, Long convocatoriaClonedId) {
    this.convocatoriaPeriodoSeguimientoCientificoRepository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaToCloneId).stream()
        .forEach(periodo -> this.convocatoriaPeriodoSeguimientoCientificoRepository
            .save(ConvocatoriaPeriodoSeguimientoCientifico.builder().convocatoriaId(convocatoriaClonedId)
                .fechaInicioPresentacion(periodo.getFechaInicioPresentacion())
                .fechaFinPresentacion(periodo.getFechaFinPresentacion()).mesInicial(periodo.getMesInicial())
                .mesFinal(periodo.getMesFinal()).numPeriodo(periodo.getNumPeriodo())
                .tipoSeguimiento(periodo.getTipoSeguimiento()).observaciones(periodo.getObservaciones()).build()));
  }

  /**
   * Se copian todos los campos del objeto {@link RequisitoIP} de la convocatoria
   * seleccionada excepto las limitaciones de fecha para las restricciones de
   * nivel académico y categoría profesional.
   *
   * Se copian: Número máximo IPs (campo "numMaximoIP") Edad máxima (campo
   * "edadMaxima") Sexo (campo "sexoRef") Listado de niveles académicos exigidos
   * (Se crean tantos registros en la tabla "RequisitoIPNivelAcademico" como tenga
   * el "RequisitoIP" de la convocatoria que se esta clonando y con el mismo dato
   * en el campo "nivelAcademicoRef", el "requisitoIP" será el registro nuevo que
   * se esta creando para la convocatoria clonada) Vinculación Universidad (sí/no)
   * (campo "vinculacionUniversidad") Listado de categorías profesionales exigidas
   * (Se crean tantos registros en la tabla "RequisitoIPCategoria como tenga el
   * "RequisitoIP" de la convocatoria que se esta clonando y con el mismo dato en
   * el campo "categoriaProfesionalRef", el "requisitoIP" será el registro nuevo
   * que se esta creando para la convocatoria clonada) Número mínimo proyectos
   * competitivos (campo "numMinimoCompetitivos") Número mínimo proyectos no
   * competitivos (campo "numMinimoNoCompetitivos") Número máximo proyectos
   * competitivos en curso (campo "numMaximoCompetitivosActivos") Número máximo
   * proyectos no competitivos en curso (campo "numMaximoNoCompetitivosActivos")
   * Observaciones sobre los requisitos a cumplir (campo "otrosRequisitos") No se
   * copian Limitaciones sobre la fecha de obtención del nivel académico
   * ("anterior a" y "posterior a") (campos "fechaMaximaNivelAcademico" y
   * "fechaMinimaNivelAcademico") Limitaciones sobre la fecha de obtención de la
   * categoría profesional ("anterior a" y "posterior a") (campos
   * "fechaMaximaCategoriaProfecional" y "fechaMinimaCategoriaProfecional"")
   *
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneRequisitoIP(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    Optional<RequisitoIP> requisito = this.requisitoIPRepository.findById(convocatoriaToCloneId);

    if (!requisito.isPresent()) {
      return;
    }
    this.requisitoIPRepository.save(createRequisitoIP(requisito.get(), convocatoriaClonedId));

    this.cloneRequisitoIPNivelesAcademicos(convocatoriaToCloneId, convocatoriaClonedId);
    this.cloneRequisitoIPCategoriasProfesionales(convocatoriaToCloneId, convocatoriaClonedId);
  }

  private RequisitoIP createRequisitoIP(RequisitoIP requisito, Long convocatoriaClonedId) {

    return RequisitoIP.builder().id(convocatoriaClonedId).numMaximoIP(requisito.getNumMaximoIP())
        .edadMaxima(requisito.getEdadMaxima()).sexoRef(requisito.getSexoRef())
        .vinculacionUniversidad(requisito.getVinculacionUniversidad())
        .numMinimoCompetitivos(requisito.getNumMinimoCompetitivos())
        .numMinimoNoCompetitivos(requisito.getNumMinimoNoCompetitivos())
        .numMaximoCompetitivosActivos(requisito.getNumMaximoCompetitivosActivos())
        .numMaximoNoCompetitivosActivos(requisito.getNumMaximoNoCompetitivosActivos())
        .otrosRequisitos(requisito.getOtrosRequisitos()).build();
  }

  /**
   * Listado de niveles académicos exigidos (Se crean tantos registros en la tabla
   * "RequisitoIPNivelAcademico" como tenga el "RequisitoIP" de la convocatoria
   * que se esta clonando y con el mismo dato en el campo "nivelAcademicoRef", el
   * "requisitoIP" será el registro nuevo que se esta creando para la convocatoria
   * clonada)
   *
   * @param requisitoIPToClneId id del {@link RequisitoIP} que se está clonando
   * @param requisitoIPClonedId id del {@link RequisitoIP} clonado
   */
  private void cloneRequisitoIPNivelesAcademicos(Long requisitoIPToClneId, Long requisitoIPClonedId) {
    this.requisitoIPNivelAcademicoRepository.findByRequisitoIPId(requisitoIPToClneId).stream()
        .forEach(nivel -> this.requisitoIPNivelAcademicoRepository.save(RequisitoIPNivelAcademico.builder()
            .requisitoIPId(requisitoIPClonedId).nivelAcademicoRef(nivel.getNivelAcademicoRef()).build()));
  }

  /**
   * Listado de {@link RequisitoIPCategoriaProfesional} exigidas (Se crean tantos
   * registros en la tabla "RequisitoIPCategoria como tenga el "RequisitoIP" de la
   * convocatoria que se esta clonando y con el mismo dato en el campo
   * "categoriaProfesionalRef", el "requisitoIP" será el registro nuevo que se
   * esta creando para la convocatoria clonada)
   *
   * @param requisitoIPToClneId id del {@link RequisitoIP} que se está clonando
   * @param requisitoIPClonedId id del {@link RequisitoIP} clonado
   */
  private void cloneRequisitoIPCategoriasProfesionales(Long requisitoIPToClneId, Long requisitoIPClonedId) {
    this.requisitoIPCategoriaProfesionalRepository.findByRequisitoIPId(requisitoIPToClneId).stream()
        .forEach(categoria -> this.requisitoIPCategoriaProfesionalRepository
            .save(RequisitoIPCategoriaProfesional.builder().requisitoIPId(requisitoIPClonedId)
                .categoriaProfesionalRef(categoria.getCategoriaProfesionalRef()).build()));
  }

  /**
   * Se copian todos los campos de la tabla "RequisitoEqupo" de la convocatoria
   * seleccionada excepto las limitaciones de fecha para las restricciones de
   * nivel académico y categoría profesional. Se copian: Edad máxima (campo
   * "edadMaxima") Sexo (campo "sexoRef") Ratio mínimo exigido (campo "ratioSexo")
   * Listado de niveles académicos exigidos (Se crean tantos registros en la tabla
   * "RequisitoEquipoNivelAcademico" como tenga el "RequisitoEquipo" de la
   * convocatoria que se esta clonando y con el mismo dato en el campo
   * "nivelAcademicoRef", el "requisitoEquipo" será el registro nuevo que se esta
   * creando para la convocatoria clonada) Vinculación Universidad (sí/no) (campo
   * "vinculacionUniversidad") Listado de categorías profesionales exigidas (Se
   * crean tantos registros en la tabla "RequisitoEquipoCategoria como tenga el
   * "RequisitoEquipo" de la convocatoria que se esta clonando y con el mismo dato
   * en el campo "categoriaProfesionalRef", el "requisitoEquipo" será el registro
   * nuevo que se esta creando para la convocatoria clonada) Número mínimo
   * proyectos competitivos (campo "numMinimoCompetitivos") Número mínimo
   * proyectos no competitivos (campo "numMinimoNoCompetitivos") Número máximo
   * proyectos competitivos en curso (campo "numMaximoCompetitivosActivos") Número
   * máximo proyectos no competitivos en curso (campo
   * "numMaximoNoCompetitivosActivos") Observaciones sobre los requisitos a
   * cumplir (campo "otrosRequisitos")
   *
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneRequisitosEquipo(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    Optional<RequisitoEquipo> requisito = this.requisitoEquipoRepository.findByConvocatoriaId(convocatoriaToCloneId);
    if (!requisito.isPresent()) {
      return;
    }

    this.requisitoEquipoRepository.save(createRequisitoEquipo(convocatoriaClonedId, requisito.get()));

    this.cloneRequisitoEquipoNivelesAcademicos(convocatoriaToCloneId, convocatoriaClonedId);
    this.cloneRequisitoEquipoCategoriasProfesionales(convocatoriaToCloneId, convocatoriaClonedId);
  }

  private RequisitoEquipo createRequisitoEquipo(Long convocatoriaClonedId, RequisitoEquipo requisito) {

    return RequisitoEquipo.builder().edadMaxima(requisito.getEdadMaxima()).sexoRef(requisito.getSexoRef())
        .ratioSexo(requisito.getRatioSexo()).vinculacionUniversidad(requisito.getVinculacionUniversidad())
        .numMinimoCompetitivos(requisito.getNumMinimoCompetitivos())
        .numMinimoNoCompetitivos(requisito.getNumMinimoNoCompetitivos())
        .numMaximoCompetitivosActivos(requisito.getNumMaximoNoCompetitivosActivos())
        .numMaximoNoCompetitivosActivos(requisito.getNumMaximoNoCompetitivosActivos())
        .otrosRequisitos(requisito.getOtrosRequisitos()).id(convocatoriaClonedId).build();
  }

  /**
   * Listado de niveles académicos exigidos (Se crean tantos registros en la tabla
   * "RequisitoEquipoNivelAcademico" como tenga el "RequisitoEquipo" de la
   * convocatoria que se esta clonando y con el mismo dato en el campo
   * "nivelAcademicoRef", el "requisitoEquipo" será el registro nuevo que se esta
   * creando para la convocatoria clonada)
   *
   * @param requisitoEquipoToClneId id del {@link RequisitoIP} que se está
   *                                clonando
   * @param requisitoEquipoClonedId id del {@link RequisitoIP} clonado
   */
  private void cloneRequisitoEquipoNivelesAcademicos(Long requisitoEquipoToClneId, Long requisitoEquipoClonedId) {
    this.requisitoEquipoNivelAcademicoRepository.findByRequisitoEquipoId(requisitoEquipoToClneId).stream()
        .forEach(nivel -> this.requisitoEquipoNivelAcademicoRepository.save(RequisitoEquipoNivelAcademico.builder()
            .requisitoEquipoId(requisitoEquipoClonedId).nivelAcademicoRef(nivel.getNivelAcademicoRef()).build()));
  }

  /**
   * Listado de categorías profesionales exigidas (Se crean tantos registros en la
   * tabla "RequisitoEquipoCategoria como tenga el "RequisitoEquipo" de la
   * convocatoria que se esta clonando y con el mismo dato en el campo
   * "categoriaProfesionalRef", el "requisitoEquipo" será el registro nuevo que se
   * esta creando para la convocatoria clonada)
   *
   * @param requisitoEquipoToClneId id del {@link RequisitoEquipo} que se está
   *                                clonando
   * @param requisitoEquipoClonedId id del {@link RequisitoEquipo} clonado
   */
  private void cloneRequisitoEquipoCategoriasProfesionales(Long requisitoEquipoToClneId, Long requisitoEquipoClonedId) {
    this.requisitoEquipoCategoriaProfesionalRepository.findByRequisitoEquipoId(requisitoEquipoToClneId).stream()
        .forEach(categoria -> this.requisitoEquipoCategoriaProfesionalRepository
            .save(RequisitoEquipoCategoriaProfesional.builder().requisitoEquipoId(requisitoEquipoClonedId)
                .categoriaProfesionalRef(categoria.getCategoriaProfesionalRef()).build()));
  }

  /**
   * Se copian todos los datos de las tablas "ConvocatoriaConceptoGasto" y
   * "ConvocatoriaConceptoGastoCodigoEc" Mes inicial (campo "mesInicial" de la
   * tabla "ConvocatoriaConceptoGasto") Mes final (campo "mesFinal" de la tabla
   * "ConvocatoriaConceptoGasto") Importe máximo permitido (campo "importeMaximo"
   * de la tabla "ConvocatoriaConceptoGasto") Observaciones (campo "observaciones"
   * de la tabla "ConvocatoriaConceptoGasto") Permitido (campo "permitido" de la
   * tabla "ConvocatoriaConceptoGasto") Listado de códigos económicos vinculados :
   * Se copian todos los códigos económicos vinculados al concepto de gasto con
   * los mismos datos (código económico, fecha inicio, fecha fin y observaciones).
   * Se crean tantos registros en la tabla "ConvocatoriaConceptoGastoCodigoEc"
   * como tenga la convocatoria que se esta clonando y con los mismos datos en los
   * campos "codigoEconomicoRef", "fechaInicio", "fechaFin" y "observaciones", el
   * concepto de gasto será será el identificador del concepto de gasto que se
   * esta creado, por cada concepto de gasto de la convocatoria clonada)
   *
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc(Long convocatoriaToCloneId,
      Long convocatoriaClonedId) {
    this.convocatoriaConceptoGastoRepository.findByConvocatoriaId(convocatoriaToCloneId).stream()
        .forEach(convConceptoGasto -> {
          ConvocatoriaConceptoGasto clonedConvocatoriaConceptoGasto = this.convocatoriaConceptoGastoRepository
              .save(createConvocatoriaConceptoGasto(convocatoriaClonedId, convConceptoGasto));
          this.convocatoriaConceptoGastoCodigoEcRepository
              .findAllByConvocatoriaConceptoGastoId(convConceptoGasto.getId()).stream()
              .forEach(convConceptoGastoCodEc -> this.convocatoriaConceptoGastoCodigoEcRepository.save(
                  createConvocatoriaConceptoGastoCodigoEc(clonedConvocatoriaConceptoGasto, convConceptoGastoCodEc)));
        });
  }

  private ConvocatoriaConceptoGastoCodigoEc createConvocatoriaConceptoGastoCodigoEc(
      ConvocatoriaConceptoGasto clonedConvocatoriaConceptoGasto,
      ConvocatoriaConceptoGastoCodigoEc convConceptoGastoCodEc) {
    return ConvocatoriaConceptoGastoCodigoEc.builder()
        .convocatoriaConceptoGastoId(clonedConvocatoriaConceptoGasto.getId())
        .codigoEconomicoRef(convConceptoGastoCodEc.getCodigoEconomicoRef())
        .fechaInicio(convConceptoGastoCodEc.getFechaInicio()).fechaFin(convConceptoGastoCodEc.getFechaFin())
        .observaciones(convConceptoGastoCodEc.getObservaciones()).build();
  }

  private ConvocatoriaConceptoGasto createConvocatoriaConceptoGasto(Long convocatoriaClonedId,
      ConvocatoriaConceptoGasto convConceptoGasto) {
    return ConvocatoriaConceptoGasto.builder().convocatoriaId(convocatoriaClonedId)
        .mesInicial(convConceptoGasto.getMesInicial()).mesFinal(convConceptoGasto.getMesFinal())
        .importeMaximo(convConceptoGasto.getImporteMaximo()).observaciones(convConceptoGasto.getObservaciones())
        .permitido(convConceptoGasto.getPermitido()).conceptoGasto(convConceptoGasto.getConceptoGasto()).build();
  }

  /**
   * Se copian todas las partidas y todos sus datos. Se crean tantos registros de
   * tipo {@link ConvocatoriaPartida} como tenga la convocatoria que se esta
   * clonando y con los mismos datos en los campos "codigo", "descripcion" y
   * "tipoPartida", la convocatoria será el identificador de la convocatoria que
   * se esta creando
   *
   * @param convocatoriaToCloneId id de la {@link Convocatoria} que se está
   *                              clonando
   * @param convocatoriaClonedId  id de la {@link Convocatoria} clon
   */
  public void clonePartidasPresupuestarias(Long convocatoriaToCloneId, Long convocatoriaClonedId) {

    this.convocatoriaPartidaRepository.findByConvocatoriaId(convocatoriaToCloneId).stream()
        .forEach(partida -> this.convocatoriaPartidaRepository
            .save(ConvocatoriaPartida.builder().convocatoriaId(convocatoriaClonedId).codigo(partida.getCodigo())
                .descripcion(partida.getDescripcion()).tipoPartida(partida.getTipoPartida()).build()));
  }
}
