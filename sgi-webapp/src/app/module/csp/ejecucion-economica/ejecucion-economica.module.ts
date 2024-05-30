import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { DetalleEconomicoComponent } from './common/detalle-economico/detalle-economico.component';
import { FilterFechasComponent } from './common/filter-fechas/filter-fechas.component';
import { TableProyectosRelacionadosComponent } from './common/table-proyectos-relacionados/table-proyectos-relacionados.component';
import { EjecucionEconomicaDataResolver } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { ClasificacionGastosComponent } from './ejecucion-economica-formulario/clasificacion-gastos/clasificacion-gastos.component';
import { DetalleOperacionesGastosComponent } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.component';
import { DetalleOperacionesGastosExportModalComponent } from './ejecucion-economica-formulario/detalle-operaciones-gastos/export/detalle-operaciones-gastos-export-modal.component';
import { DetalleOperacionesGastosExportService } from './ejecucion-economica-formulario/detalle-operaciones-gastos/export/detalle-operaciones-gastos-export.service';
import { DetalleOperacionesIngresosComponent } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.component';
import { DetalleOperacionesIngresosExportModalComponent } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/export/detalle-operaciones-ingresos-export-modal.component';
import { DetalleOperacionesIngresosExportService } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/export/detalle-operaciones-ingresos-export.service';
import { DetalleOperacionesModificacionesComponent } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.component';
import { DetalleOperacionesModificacionesExportModalComponent } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/export/detalle-operaciones-modificaciones-export-modal.component';
import { DetalleOperacionesModificacionesExportService } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/export/detalle-operaciones-modificaciones-export.service';
import { EjecucionPresupuestariaEstadoActualComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.component';
import { EjecucionPresupuestariaEstadoActualExportModalComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/export/ejecucion-presupuestaria-estado-actual-export-modal.component';
import { EjecucionPresupuestariaEstadoActualExportService } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/export/ejecucion-presupuestaria-estado-actual-export.service';
import { EjecucionPresupuestariaGastosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.component';
import { EjecucionPresupuestariaGastosExportModalComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/export/ejecucion-presupuestaria-gastos-export-modal.component';
import { EjecucionPresupuestariaGastosExportService } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/export/ejecucion-presupuestaria-gastos-export.service';
import { EjecucionPresupuestariaIngresosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.component';
import { EjecucionPresupuestariaIngresosExportModalComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/export/ejecucion-presupuestaria-ingresos-export-modal.component';
import { EjecucionPresupuestariaIngresosExportService } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/export/ejecucion-presupuestaria-ingresos-export.service';
import { FacturasEmitidasExportModalComponent } from './ejecucion-economica-formulario/facturas-emitidas/export/facturas-emitidas-export-modal.component';
import { FacturasEmitidasComponent } from './ejecucion-economica-formulario/facturas-emitidas/facturas-emitidas.component';
import { FacturasGastosExportModalComponent } from './ejecucion-economica-formulario/facturas-gastos/export/facturas-gastos-export-modal.component';
import { FacturasGastosExportService } from './ejecucion-economica-formulario/facturas-gastos/export/facturas-gastos-export.service';
import { FacturasGastosComponent } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.component';
import { PersonalContratadoExportModalComponent } from './ejecucion-economica-formulario/personal-contratado/export/personal-contratado-export-modal.component';
import { PersonalContratadoExportService } from './ejecucion-economica-formulario/personal-contratado/export/personal-contratado-export.service';
import { PersonalContratadoComponent } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { SeguimientoJustificacionRequerimientosComponent } from './ejecucion-economica-formulario/seguimiento-justificacion-requerimientos/seguimiento-justificacion-requerimientos.component';
import { SeguimientoJustificacionResumenComponent } from './ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.component';
import { ValidacionGastosComponent } from './ejecucion-economica-formulario/validacion-gastos/validacion-gastos.component';
import { ViajesDietasExportModalComponent } from './ejecucion-economica-formulario/viajes-dietas/export/viajes-dietas-export-modal.component';
import { ViajesDietasExportService } from './ejecucion-economica-formulario/viajes-dietas/export/viajes-dietas-export.service';
import { ViajesDietasComponent } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.component';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';
import { EjecucionEconomicaRoutingModule } from './ejecucion-economica-routing.module';
import { FacturasEmitidasModalComponent } from './modals/facturas-emitidas-modal/facturas-emitidas-modal.component';
import { FacturasGastosModalComponent } from './modals/facturas-gastos-modal/facturas-gastos-modal.component';
import { FacturasJustificantesClasificacionModal } from './modals/facturas-justificantes-clasificacion-modal/facturas-justificantes-clasificacion-modal.component';
import { IdentificadorJustificacionModalComponent } from './modals/identificador-justificacion-modal/identificador-justificacion-modal.component';
import { PersonalContratadoModalComponent } from './modals/personal-contratado-modal/personal-contratado-modal.component';
import { PresentacionDocumentacionModalComponent } from './modals/presentacion-documentacion-modal/presentacion-documentacion-modal.component';
import { RequerimientoJustificacionListadoExportModalComponent } from './modals/requerimiento-justificacion-listado-export-modal/requerimiento-justificacion-listado-export-modal.component';
import { SeguimientoGastosJustificadosResumenExportModalComponent } from './modals/seguimiento-gastos-justificados-resumen-export-modal/seguimiento-gastos-justificados-resumen-export-modal.component';
import { SeguimientoJustificacionAnualidadModalComponent } from './modals/seguimiento-justificacion-anualidad-modal/seguimiento-justificacion-anualidad-modal.component';
import { SeguimientoJustificacionModalComponent } from './modals/seguimiento-justificacion-modal/seguimiento-justificacion-modal.component';
import { ValidacionGastosEditarModalComponent } from './modals/validacion-gastos-editar-modal/validacion-gastos-editar-modal.component';
import { ValidacionGastosHistoricoModalComponent } from './modals/validacion-gastos-historico-modal/validacion-gastos-historico-modal.component';
import { ValidacionGastosModalComponent } from './modals/validacion-gastos-modal/validacion-gastos-modal.component';
import { ViajesDietasModalComponent } from './modals/viajes-dietas-modal/viajes-dietas-modal.component';
import { RequerimientoJustificacionNombrePipe } from './pipes/requerimiento-justificacion-nombre.pipe';
import { RequerimientoJustificacionGeneralListadoExportService } from './requerimiento-justificacion-general-listado-export.service';
import { RequerimientoJustificacionListadoExportService } from './requerimiento-justificacion-listado-export.service';
import { SeguimientoGastosJustificadosResumenListadoExportService } from './seguimiento-gastos-justificados-listado-export.service';
import { SeguimientoGastosJustificadosResumenListadoGeneralExportService } from './seguimiento-gastos-justificados-listado-general-export.service';
@NgModule({
  declarations: [
    ClasificacionGastosComponent,
    DetalleEconomicoComponent,
    DetalleOperacionesGastosComponent,
    DetalleOperacionesGastosExportModalComponent,
    DetalleOperacionesIngresosComponent,
    DetalleOperacionesIngresosExportModalComponent,
    DetalleOperacionesModificacionesComponent,
    DetalleOperacionesModificacionesExportModalComponent,
    EjecucionEconomicaEditarComponent,
    EjecucionEconomicaListadoComponent,
    EjecucionPresupuestariaEstadoActualComponent,
    EjecucionPresupuestariaEstadoActualExportModalComponent,
    EjecucionPresupuestariaGastosComponent,
    EjecucionPresupuestariaGastosExportModalComponent,
    EjecucionPresupuestariaIngresosComponent,
    EjecucionPresupuestariaIngresosExportModalComponent,
    FacturasEmitidasComponent,
    FacturasEmitidasExportModalComponent,
    FacturasEmitidasModalComponent,
    FacturasGastosComponent,
    FacturasGastosExportModalComponent,
    FacturasGastosModalComponent,
    FacturasJustificantesClasificacionModal,
    FilterFechasComponent,
    IdentificadorJustificacionModalComponent,
    PersonalContratadoComponent,
    PersonalContratadoExportModalComponent,
    PersonalContratadoModalComponent,
    PresentacionDocumentacionModalComponent,
    ProyectosComponent,
    RequerimientoJustificacionListadoExportModalComponent,
    RequerimientoJustificacionNombrePipe,
    SeguimientoGastosJustificadosResumenExportModalComponent,
    SeguimientoJustificacionAnualidadModalComponent,
    SeguimientoJustificacionModalComponent,
    SeguimientoJustificacionRequerimientosComponent,
    SeguimientoJustificacionResumenComponent,
    TableProyectosRelacionadosComponent,
    ValidacionGastosComponent,
    ValidacionGastosEditarModalComponent,
    ValidacionGastosHistoricoModalComponent,
    ValidacionGastosModalComponent,
    ViajesDietasComponent,
    ViajesDietasExportModalComponent,
    ViajesDietasModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    EjecucionEconomicaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgoSharedModule,
    FormlyFormsModule,
    SgpSharedModule,
    SgempSharedModule
  ],
  providers: [
    DetalleOperacionesGastosExportService,
    DetalleOperacionesIngresosExportService,
    DetalleOperacionesModificacionesExportService,
    EjecucionEconomicaDataResolver,
    EjecucionPresupuestariaEstadoActualExportService,
    EjecucionPresupuestariaGastosExportService,
    EjecucionPresupuestariaIngresosExportService,
    FacturasGastosExportService,
    LuxonDatePipe,
    PersonalContratadoExportService,
    RequerimientoJustificacionGeneralListadoExportService,
    RequerimientoJustificacionListadoExportService,
    SeguimientoGastosJustificadosResumenListadoExportService,
    SeguimientoGastosJustificadosResumenListadoGeneralExportService,
    ViajesDietasExportService
  ]
})
export class EjecucionEconomicaModule { }
