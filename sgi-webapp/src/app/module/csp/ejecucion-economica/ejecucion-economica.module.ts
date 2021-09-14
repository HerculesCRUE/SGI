import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { DetalleEconomicoComponent } from './common/detalle-economico/detalle-economico.component';
import { FilterFechasComponent } from './common/filter-fechas/filter-fechas.component';
import { TableProyectosRelacionadosComponent } from './common/table-proyectos-relacionados/table-proyectos-relacionados.component';
import { EjecucionEconomicaDataResolver } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { DetalleOperacionesGastosComponent } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.component';
import { DetalleOperacionesIngresosComponent } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.component';
import { DetalleOperacionesModificacionesComponent } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.component';
import { EjecucionPresupuestariaEstadoActualComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.component';
import { EjecucionPresupuestariaGastosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.component';
import { EjecucionPresupuestariaIngresosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.component';
import { FacturasGastosComponent } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.component';
import { PersonalContratadoComponent } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { ValidacionGastosComponent } from './ejecucion-economica-formulario/validacion-gastos/validacion-gastos.component';
import { ViajesDietasComponent } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.component';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';
import { EjecucionEconomicaRoutingModule } from './ejecucion-economica-routing.module';
import { FacturasGastosModalComponent } from './modals/facturas-gastos-modal/facturas-gastos-modal.component';
import { PersonalContratadoModalComponent } from './modals/personal-contratado-modal/personal-contratado-modal.component';
import { ValidacionGastosModalComponent } from './modals/validacion-gastos-modal/validacion-gastos-modal.component';
import { ViajesDietasModalComponent } from './modals/viajes-dietas-modal/viajes-dietas-modal.component';

@NgModule({
  declarations: [
    EjecucionEconomicaListadoComponent,
    EjecucionEconomicaEditarComponent,
    ProyectosComponent,
    EjecucionPresupuestariaEstadoActualComponent,
    EjecucionPresupuestariaGastosComponent,
    EjecucionPresupuestariaIngresosComponent,
    DetalleOperacionesGastosComponent,
    DetalleOperacionesIngresosComponent,
    DetalleOperacionesModificacionesComponent,
    FacturasGastosComponent,
    FacturasGastosModalComponent,
    ViajesDietasComponent,
    ViajesDietasModalComponent,
    PersonalContratadoComponent,
    PersonalContratadoModalComponent,
    TableProyectosRelacionadosComponent,
    FilterFechasComponent,
    DetalleEconomicoComponent,
    ValidacionGastosComponent,
    ValidacionGastosModalComponent
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
    SgpSharedModule
  ],
  providers: [
    EjecucionEconomicaDataResolver
  ]
})
export class EjecucionEconomicaModule { }
