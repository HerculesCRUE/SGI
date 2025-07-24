import { CalendarioFacturacionSgeIntegration, CardinalidadRelacionSgiSge, FacturasJustificantesColumnasFijasConfigurables, IConfiguracion, ModoEjecucion, SgeEjecucionEconomicaFiltros, SgeFiltroAnualidades, SgeIntegracionesEccMenus, ValidacionClasificacionGastos } from '@core/models/csp/configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConfiguracionResponse } from './configuracion-response';

class ConfiguracionResponseConverter
  extends SgiBaseConverter<IConfiguracionResponse, IConfiguracion> {
  toTarget(value: IConfiguracionResponse): IConfiguracion {
    if (!value) {
      return value as unknown as IConfiguracion;
    }
    return {
      id: value.id,
      altaBuscadorSgeEnabled: value.altaBuscadorSgeEnabled,
      amortizacionFondosSgeEnabled: value.amortizacionFondosSgeEnabled,
      calendarioFacturacionSgeIntegration: value.calendarioFacturacionSgeIntegration ? CalendarioFacturacionSgeIntegration[value.calendarioFacturacionSgeIntegration] : null,
      cardinalidadRelacionSgiSge: value.cardinalidadRelacionSgiSge ? CardinalidadRelacionSgiSge[value.cardinalidadRelacionSgiSge] : null,
      dedicacionMinimaGrupo: value.dedicacionMinimaGrupo,
      integracionesEccSgeEnabled: value.integracionesEccSgeEnabled?.split(',').map(ecc => SgeIntegracionesEccMenus[ecc]),
      ejecucionEconomicaGruposEnabled: value.ejecucionEconomicaGruposEnabled,
      facturasGastosColumnasFijasVisibles: value.facturasGastosColumnasFijasVisibles?.split(',').map(s => FacturasJustificantesColumnasFijasConfigurables[s]),
      formatoAnualidadAnio: value.formatoAnualidadAnio,
      formatoCodigoInternoProyecto: value.formatoCodigoInternoProyecto,
      formatoIdentificadorJustificacion: value.formatoIdentificadorJustificacion,
      formatoPartidaPresupuestaria: value.formatoPartidaPresupuestaria,
      gastosJustificadosSgeEnabled: value.gastosJustificadosSgeEnabled,
      modificacionProyectoSgeEnabled: value.modificacionProyectoSgeEnabled,
      notificacionPresupuestoSgeEnabled: value.notificacionPresupuestoSgeEnabled,
      partidasPresupuestariasSgeEnabled: value.partidasPresupuestariasSgeEnabled,
      personalContratadoColumnasFijasVisibles: value.personalContratadoColumnasFijasVisibles?.split(',').map(s => FacturasJustificantesColumnasFijasConfigurables[s]),
      plantillaFormatoCodigoInternoProyecto: value.plantillaFormatoCodigoInternoProyecto,
      plantillaFormatoIdentificadorJustificacion: value.plantillaFormatoIdentificadorJustificacion,
      plantillaFormatoPartidaPresupuestaria: value.plantillaFormatoPartidaPresupuestaria,
      proyectoSgeAltaModoEjecucion: value.proyectoSgeAltaModoEjecucion ? ModoEjecucion[value.proyectoSgeAltaModoEjecucion] : null,
      proyectoSgeModificacionModoEjecucion: value.proyectoSgeModificacionModoEjecucion ? ModoEjecucion[value.proyectoSgeModificacionModoEjecucion] : null,
      proyectoSocioPaisFilterEnabled: value.proyectoSocioPaisFilterEnabled,
      sectorIvaSgeEnabled: value.sectorIvaSgeEnabled,
      sgeDetalleOperacionesGastosDetalleEnabled: value.sgeDetalleOperacionesGastosDetalleEnabled,
      sgeEjecucionPresupuestariaGastosDetalleEnabled: value.sgeEjecucionPresupuestariaGastosDetalleEnabled,
      sgeFiltroAnualidades: value.sgeFiltroAnualidades ? SgeFiltroAnualidades[value.sgeFiltroAnualidades] : null,
      solicitudesSinConvocatoriaInvestigadorEnabled: value.solicitudesSinConvocatoriaInvestigadorEnabled,
      validacionClasificacionGastos: value.validacionClasificacionGastos ? ValidacionClasificacionGastos[value.validacionClasificacionGastos] : null,
      viajesDietasColumnasFijasVisibles: value.viajesDietasColumnasFijasVisibles?.split(',').map(s => FacturasJustificantesColumnasFijasConfigurables[s]),
      sgeEjecucionEconomicaFiltros: value.sgeEjecucionEconomicaFiltros?.split(',').map(ecc => SgeEjecucionEconomicaFiltros[ecc]),
      sgeEliminarRelacionProyectoEnabled: value.sgeEliminarRelacionProyectoEnabled
    };
  }

  fromTarget(value: IConfiguracion): IConfiguracionResponse {
    if (!value) {
      return value as unknown as IConfiguracionResponse;
    }
    return {
      id: value.id,
      altaBuscadorSgeEnabled: value.altaBuscadorSgeEnabled,
      amortizacionFondosSgeEnabled: value.amortizacionFondosSgeEnabled,
      calendarioFacturacionSgeIntegration: value.calendarioFacturacionSgeIntegration,
      cardinalidadRelacionSgiSge: value.cardinalidadRelacionSgiSge,
      dedicacionMinimaGrupo: value.dedicacionMinimaGrupo,
      integracionesEccSgeEnabled: JSON.stringify(value.integracionesEccSgeEnabled?.join(',')),
      ejecucionEconomicaGruposEnabled: value.ejecucionEconomicaGruposEnabled,
      facturasGastosColumnasFijasVisibles: value.facturasGastosColumnasFijasVisibles?.join(','),
      formatoAnualidadAnio: value.formatoAnualidadAnio,
      formatoCodigoInternoProyecto: value.formatoCodigoInternoProyecto,
      formatoIdentificadorJustificacion: value.formatoIdentificadorJustificacion,
      formatoPartidaPresupuestaria: value.formatoPartidaPresupuestaria,
      gastosJustificadosSgeEnabled: value.gastosJustificadosSgeEnabled,
      modificacionProyectoSgeEnabled: value.modificacionProyectoSgeEnabled,
      notificacionPresupuestoSgeEnabled: value.notificacionPresupuestoSgeEnabled,
      partidasPresupuestariasSgeEnabled: value.partidasPresupuestariasSgeEnabled,
      personalContratadoColumnasFijasVisibles: value.personalContratadoColumnasFijasVisibles?.join(','),
      plantillaFormatoCodigoInternoProyecto: value.plantillaFormatoCodigoInternoProyecto,
      plantillaFormatoIdentificadorJustificacion: value.plantillaFormatoIdentificadorJustificacion,
      plantillaFormatoPartidaPresupuestaria: value.plantillaFormatoPartidaPresupuestaria,
      proyectoSgeAltaModoEjecucion: value.proyectoSgeAltaModoEjecucion,
      proyectoSgeModificacionModoEjecucion: value.proyectoSgeModificacionModoEjecucion,
      proyectoSocioPaisFilterEnabled: value.proyectoSocioPaisFilterEnabled,
      sectorIvaSgeEnabled: value.sectorIvaSgeEnabled,
      sgeDetalleOperacionesGastosDetalleEnabled: value.sgeDetalleOperacionesGastosDetalleEnabled,
      sgeEjecucionPresupuestariaGastosDetalleEnabled: value.sgeEjecucionPresupuestariaGastosDetalleEnabled,
      sgeFiltroAnualidades: value.sgeFiltroAnualidades,
      solicitudesSinConvocatoriaInvestigadorEnabled: value.solicitudesSinConvocatoriaInvestigadorEnabled,
      validacionClasificacionGastos: value.validacionClasificacionGastos,
      viajesDietasColumnasFijasVisibles: value.viajesDietasColumnasFijasVisibles?.join(','),
      sgeEjecucionEconomicaFiltros: JSON.stringify(value.integracionesEccSgeEnabled?.join(',')),
      sgeEliminarRelacionProyectoEnabled: value.sgeEliminarRelacionProyectoEnabled
    };
  }
}

export const CONFIGURACION_RESPONSE_CONVERTER = new ConfiguracionResponseConverter();
