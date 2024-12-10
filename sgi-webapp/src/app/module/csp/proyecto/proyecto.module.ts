import { CommonModule, DecimalPipe, PercentPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgeSharedModule } from 'src/app/esb/sge/shared/sge-shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PiiSharedModule } from '../../pii/shared/pii-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { HistoricoIpModalComponent } from './modals/historico-ip-modal/historico-ip-modal.component';
import { ProyectoCalendarioFacturacionModalComponent } from './modals/proyecto-calendario-facturacion-modal/proyecto-calendario-facturacion-modal.component';
import { CambioEstadoModalComponent } from './modals/proyecto-cambio-estado-modal/cambio-estado-modal.component';
import { ProyectoContextoModalComponent } from './modals/proyecto-contexto-modal/proyecto-contexto-modal.component';
import { ProyectoCopiarAparatadosModalComponent } from './modals/proyecto-copiar-apartados-modal/proyecto-copiar-apartados-modal.component';
import { ProyectoEntidadConvocanteModalComponent } from './modals/proyecto-entidad-convocante-modal/proyecto-entidad-convocante-modal.component';
import { ProyectoHitosModalComponent } from './modals/proyecto-hitos-modal/proyecto-hitos-modal.component';
import { ProyectoInfoModificarFechasModalComponent } from './modals/proyecto-info-modificar-fechas-modal/proyecto-info-modificar-fechas-modal.component';
import { ProyectoListadoExportModalComponent } from './modals/proyecto-listado-export-modal/proyecto-listado-export-modal.component';
import { ProyectoPaquetesTrabajoModalComponent } from './modals/proyecto-paquetes-trabajo-modal/proyecto-paquetes-trabajo-modal.component';
import { ProyectoPeriodoAmortizacionModalComponent } from './modals/proyecto-periodo-amortizacion-fondos-modal/proyecto-periodo-justificacion-modal/proyecto-periodo-amortizacion-fondos-modal.component';
import { ProyectoPeriodoJustificacionModalComponent } from './modals/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal.component';
import { ProyectoPlazosModalComponent } from './modals/proyecto-plazos-modal/proyecto-plazos-modal.component';
import { ProyectoRelacionModalComponent } from './modals/proyecto-relacion-modal/proyecto-relacion-modal.component';
import { ProyectoResponsableEconomicoModalComponent } from './modals/proyecto-responsable-economico-modal/proyecto-responsable-economico-modal.component';
import { ProyectoEntidadConvocantePlanPipe } from './pipes/proyecto-entidad-convocante-plan.pipe';
import { ProyectoAreaConocimientoListadoExportService } from './proyecto-area-conocimiento-listado-export.service';
import { ProyectoCalendarioFacturacionListadoExportService } from './proyecto-calendario-facturacion-listado-export.service';
import { ProyectoCalendarioJustificacionListadoExportService } from './proyecto-calendario-justificacion-listado-export.service';
import { ProyectoClasificacionListadoExportService } from './proyecto-clasificacion-listado-export.service';
import { ProyectoConceptoGastoListadoExportService } from './proyecto-concepto-gasto-listado-export.service';
import { ProyectoConvocatoriaListadoExportService } from './proyecto-convocatoria-listado-export.service';
import { ProyectoCrearComponent } from './proyecto-crear/proyecto-crear.component';
import { ProyectoDataResolver } from './proyecto-data.resolver';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoEntidadConvocanteListadoExportService } from './proyecto-entidad-convocante-listado-export.service';
import { ProyectoEntidadFinanciadoraListadoExportService } from './proyecto-entidad-financiadora-listado-export.service';
import { ProyectoEntidadGestoraListadoExportService } from './proyecto-entidad-gestora-listado-export.service';
import { ProyectoEquipoListadoExportService } from './proyecto-equipo-listado-export.service';
import { ProyectoFooterListadoExportService } from './proyecto-footer-listado-export.service';
import { ProyectoAgrupacionesGastoComponent } from './proyecto-formulario/proyecto-agrupaciones-gasto/proyecto-agrupaciones-gasto.component';
import { ProyectoAmortizacionFondosComponent } from './proyecto-formulario/proyecto-amortizacion-fondos/proyecto-amortizacion-fondos.component';
import { ProyectoAreaConocimientoComponent } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.component';
import { ProyectoCalendarioFacturacionComponent } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.component';
import { ProyectoCalendarioJustificacionComponent } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.component';
import { ProyectoClasificacionesComponent } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.component';
import { ProyectoConceptosGastoComponent } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.component';
import { ProyectoConsultaPresupuestoExportModalComponent } from './proyecto-formulario/proyecto-consulta-presupuesto/export/proyecto-consulta-presupuesto-export-modal.component';
import { ProyectoConsultaPresupuestoExportService } from './proyecto-formulario/proyecto-consulta-presupuesto/export/proyecto-consulta-presupuesto-export.service';
import { ProyectoConsultaPresupuestoComponent } from './proyecto-formulario/proyecto-consulta-presupuesto/proyecto-consulta-presupuesto.component';
import { ProyectoContextoComponent } from './proyecto-formulario/proyecto-contexto/proyecto-contexto.component';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { ProyectoDocumentosComponent } from './proyecto-formulario/proyecto-documentos/proyecto-documentos.component';
import { ProyectoEntidadGestoraComponent } from './proyecto-formulario/proyecto-entidad-gestora/proyecto-entidad-gestora.component';
import { ProyectoEntidadesConvocantesComponent } from './proyecto-formulario/proyecto-entidades-convocantes/proyecto-entidades-convocantes.component';
import { ProyectoEntidadesFinanciadorasComponent } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.component';
import { ProyectoEquipoComponent } from './proyecto-formulario/proyecto-equipo/proyecto-equipo.component';
import { ProyectoHistoricoEstadosComponent } from './proyecto-formulario/proyecto-historico-estados/proyecto-historico-estados.component';
import { ProyectoHitosComponent } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.component';
import { ProyectoPaqueteTrabajoComponent } from './proyecto-formulario/proyecto-paquete-trabajo/proyecto-paquete-trabajo.component';
import { ProyectoPartidasPresupuestariasComponent } from './proyecto-formulario/proyecto-partidas-presupuestarias/proyecto-partidas-presupuestarias.component';
import { ProyectoPeriodoSeguimientosComponent } from './proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.component';
import { ProyectoPlazosComponent } from './proyecto-formulario/proyecto-plazos/proyecto-plazos.component';
import { ProyectoPresupuestoComponent } from './proyecto-formulario/proyecto-presupuesto/proyecto-presupuesto.component';
import { ProyectoProrrogasComponent } from './proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.component';
import { ProyectoProyectosSgeComponent } from './proyecto-formulario/proyecto-proyectos-sge/proyecto-proyectos-sge.component';
import { ProyectoRelacionesComponent } from './proyecto-formulario/proyecto-relaciones/proyecto-relaciones.component';
import { ProyectoResponsableEconomicoComponent } from './proyecto-formulario/proyecto-responsable-economico/proyecto-responsable-economico.component';
import { ProyectoSociosComponent } from './proyecto-formulario/proyecto-socios/proyecto-socios.component';
import { ProyectoGeneralListadoExportService } from './proyecto-general-listado-export.service';
import { ProyectoGruposInvestigacionIpListadoExportService } from './proyecto-grupos-investigacion-ips-listado-export.service';
import { ProyectoHeaderListadoExportService } from './proyecto-header-listado-export.service';
import { ProyectoListadoExportService } from './proyecto-listado-export.service';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';
import { ProyectoPartidaPresupuestariaListadoExportService } from './proyecto-partida-presupuestaria-listado-export.service';
import { ProyectoPeriodoSeguimientoListadoExportService } from './proyecto-periodo-seguimiento-listado-export.service';
import { ProyectoPresupuestoListadoExportService } from './proyecto-presupuesto-listado-export.service';
import { ProyectoProrrogaListadoExportService } from './proyecto-prorroga-listado-export.service';
import { ProyectoRelacionListadoExportService } from './proyecto-relacion-listado-export.service';
import { ProyectoResponsableEconomicoListadoExportService } from './proyecto-responsable-economico-listado-export.service';
import { ProyectoRoutingModule } from './proyecto-routing.module';
import { ProyectoSocioListadoExportService } from './proyecto-socio-listado-export.service';
import { ProyectoSolicitudListadoExportService } from './proyecto-solicitud-listado-export.service';

@NgModule({
  declarations: [
    CambioEstadoModalComponent,
    HistoricoIpModalComponent,
    ProyectoAgrupacionesGastoComponent,
    ProyectoAmortizacionFondosComponent,
    ProyectoAreaConocimientoComponent,
    ProyectoCalendarioFacturacionComponent,
    ProyectoCalendarioFacturacionModalComponent,
    ProyectoCalendarioJustificacionComponent,
    ProyectoClasificacionesComponent,
    ProyectoConceptosGastoComponent,
    ProyectoConsultaPresupuestoComponent,
    ProyectoConsultaPresupuestoExportModalComponent,
    ProyectoContextoComponent,
    ProyectoContextoModalComponent,
    ProyectoCopiarAparatadosModalComponent,
    ProyectoCrearComponent,
    ProyectoDocumentosComponent,
    ProyectoEditarComponent,
    ProyectoEntidadConvocanteModalComponent,
    ProyectoEntidadConvocantePlanPipe,
    ProyectoEntidadesConvocantesComponent,
    ProyectoEntidadesFinanciadorasComponent,
    ProyectoEntidadGestoraComponent,
    ProyectoEquipoComponent,
    ProyectoFichaGeneralComponent,
    ProyectoHistoricoEstadosComponent,
    ProyectoHitosComponent,
    ProyectoHitosModalComponent,
    ProyectoInfoModificarFechasModalComponent,
    ProyectoListadoComponent,
    ProyectoListadoExportModalComponent,
    ProyectoPaquetesTrabajoModalComponent,
    ProyectoPaqueteTrabajoComponent,
    ProyectoPartidasPresupuestariasComponent,
    ProyectoPeriodoAmortizacionModalComponent,
    ProyectoPeriodoJustificacionModalComponent,
    ProyectoPeriodoSeguimientosComponent,
    ProyectoPlazosComponent,
    ProyectoPlazosModalComponent,
    ProyectoPresupuestoComponent,
    ProyectoProrrogasComponent,
    ProyectoProyectosSgeComponent,
    ProyectoRelacionesComponent,
    ProyectoRelacionModalComponent,
    ProyectoResponsableEconomicoComponent,
    ProyectoResponsableEconomicoModalComponent,
    ProyectoSociosComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    PiiSharedModule,
    ProyectoRoutingModule,
    ReactiveFormsModule,
    SgempSharedModule,
    SgeSharedModule,
    SgiAuthModule,
    SgoSharedModule,
    SgpSharedModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    DecimalPipe,
    LuxonDatePipe,
    PercentPipe,
    ProyectoAreaConocimientoListadoExportService,
    ProyectoCalendarioFacturacionListadoExportService,
    ProyectoCalendarioJustificacionListadoExportService,
    ProyectoClasificacionListadoExportService,
    ProyectoConceptoGastoListadoExportService,
    ProyectoConsultaPresupuestoExportService,
    ProyectoConvocatoriaListadoExportService,
    ProyectoDataResolver,
    ProyectoEntidadConvocanteListadoExportService,
    ProyectoEntidadConvocantePlanPipe,
    ProyectoEntidadFinanciadoraListadoExportService,
    ProyectoEntidadGestoraListadoExportService,
    ProyectoEquipoListadoExportService,
    ProyectoFooterListadoExportService,
    ProyectoGeneralListadoExportService,
    ProyectoGruposInvestigacionIpListadoExportService,
    ProyectoHeaderListadoExportService,
    ProyectoListadoExportService,
    ProyectoPartidaPresupuestariaListadoExportService,
    ProyectoPeriodoSeguimientoListadoExportService,
    ProyectoPresupuestoListadoExportService,
    ProyectoProrrogaListadoExportService,
    ProyectoRelacionListadoExportService,
    ProyectoResponsableEconomicoListadoExportService,
    ProyectoSocioListadoExportService,
    ProyectoSolicitudListadoExportService
  ]
})
export class ProyectoModule { }
