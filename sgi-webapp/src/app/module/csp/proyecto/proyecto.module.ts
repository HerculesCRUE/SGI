import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PiiSharedModule } from '../../pii/shared/pii-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { HistoricoIpModalComponent } from './modals/historico-ip-modal/historico-ip-modal.component';
import { ProyectoCalendarioFacturacionModalComponent } from './modals/proyecto-calendario-facturacion-modal/proyecto-calendario-facturacion-modal.component';
import { CambioEstadoModalComponent } from './modals/proyecto-cambio-estado-modal/cambio-estado-modal.component';
import { ProyectoContextoModalComponent } from './modals/proyecto-contexto-modal/proyecto-contexto-modal.component';
import { ProyectoEntidadConvocanteModalComponent } from './modals/proyecto-entidad-convocante-modal/proyecto-entidad-convocante-modal.component';
import { ProyectoHitosModalComponent } from './modals/proyecto-hitos-modal/proyecto-hitos-modal.component';
import { ProyectoListadoModalComponent } from './modals/proyecto-listado-modal/proyecto-listado-modal.component';
import { ProyectoPaquetesTrabajoModalComponent } from './modals/proyecto-paquetes-trabajo-modal/proyecto-paquetes-trabajo-modal.component';
import { ProyectoPeriodoJustificacionModalComponent } from './modals/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal/proyecto-periodo-justificacion-modal.component';
import { ProyectoPlazosModalComponent } from './modals/proyecto-plazos-modal/proyecto-plazos-modal.component';
import { ProyectoRelacionModalComponent } from './modals/proyecto-relacion-modal/proyecto-relacion-modal.component';
import { ProyectoResponsableEconomicoModalComponent } from './modals/proyecto-responsable-economico-modal/proyecto-responsable-economico-modal.component';
import { ProyectoEntidadConvocantePlanPipe } from './pipes/proyecto-entidad-convocante-plan.pipe';
import { ProyectoCrearComponent } from './proyecto-crear/proyecto-crear.component';
import { ProyectoDataResolver } from './proyecto-data.resolver';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoAgrupacionesGastoComponent } from './proyecto-formulario/proyecto-agrupaciones-gasto/proyecto-agrupaciones-gasto.component';
import { ProyectoAreaConocimientoComponent } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.component';
import { ProyectoCalendarioFacturacionComponent } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.component';
import { ProyectoCalendarioJustificacionComponent } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.component';
import { ProyectoClasificacionesComponent } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.component';
import { ProyectoConceptosGastoComponent } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.component';
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
import { ProyectoListadoService } from './proyecto-listado.service';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';
import { ProyectoRoutingModule } from './proyecto-routing.module';

@NgModule({
  declarations: [
    CambioEstadoModalComponent,
    ProyectoListadoComponent,
    ProyectoListadoModalComponent,
    ProyectoCrearComponent,
    ProyectoEditarComponent,
    ProyectoFichaGeneralComponent,
    ProyectoEntidadesFinanciadorasComponent,
    ProyectoHitosComponent,
    ProyectoHitosModalComponent,
    ProyectoSociosComponent,
    ProyectoEntidadesConvocantesComponent,
    ProyectoEntidadConvocantePlanPipe,
    ProyectoEntidadConvocanteModalComponent,
    ProyectoPaqueteTrabajoComponent,
    ProyectoPaquetesTrabajoModalComponent,
    ProyectoPlazosComponent,
    ProyectoPlazosModalComponent,
    ProyectoContextoComponent,
    ProyectoContextoModalComponent,
    ProyectoPeriodoSeguimientosComponent,
    ProyectoEntidadGestoraComponent,
    ProyectoEquipoComponent,
    ProyectoProrrogasComponent,
    ProyectoHistoricoEstadosComponent,
    ProyectoDocumentosComponent,
    ProyectoClasificacionesComponent,
    ProyectoAreaConocimientoComponent,
    ProyectoProyectosSgeComponent,
    ProyectoPartidasPresupuestariasComponent,
    ProyectoConceptosGastoComponent,
    ProyectoPresupuestoComponent,
    ProyectoResponsableEconomicoComponent,
    ProyectoResponsableEconomicoModalComponent,
    ProyectoAgrupacionesGastoComponent,
    ProyectoCalendarioJustificacionComponent,
    ProyectoPeriodoJustificacionModalComponent,
    ProyectoConsultaPresupuestoComponent,
    ProyectoRelacionesComponent,
    ProyectoRelacionModalComponent,
    ProyectoCalendarioFacturacionComponent,
    ProyectoCalendarioFacturacionModalComponent,
    HistoricoIpModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule,
    SgoSharedModule,
    SgpSharedModule,
    SgempSharedModule,
    PiiSharedModule
  ],
  providers: [
    ProyectoDataResolver,
    ProyectoEntidadConvocantePlanPipe,
    ProyectoListadoService
  ]
})
export class ProyectoModule { }
