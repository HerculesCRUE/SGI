import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ProyectoAnualidadGastoModalComponent } from './modals/proyecto-anualidad-gasto-modal/proyecto-anualidad-gasto-modal.component';
import { ProyectoAnualidadIngresoModalComponent } from './modals/proyecto-anualidad-ingreso-modal/proyecto-anualidad-ingreso-modal.component';
import { ProyectoAnualidadCrearComponent } from './proyecto-anualidad-crear/proyecto-anualidad-crear.component';
import { ProyectoAnualidadDataResolver } from './proyecto-anualidad-data.resolver';
import { ProyectoAnualidadEditarComponent } from './proyecto-anualidad-editar/proyecto-anualidad-editar.component';
import { ProyectoAnualidadDatosGeneralesComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-datos-generales/proyecto-anualidad-datos-generales.component';
import { ProyectoAnualidadGastosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-gastos/proyecto-anualidad-gastos.component';
import { ProyectoAnualidadIngresosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-ingresos/proyecto-anualidad-ingresos.component';
import { ProyectoAnualidadResumenComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-resumen/proyecto-anualidad-resumen.component';
import { ProyectoAnualidadRouting } from './proyecto-anualidad-routing.module';

@NgModule({
  declarations: [ProyectoAnualidadCrearComponent, ProyectoAnualidadDatosGeneralesComponent,
    ProyectoAnualidadGastosComponent, ProyectoAnualidadGastoModalComponent,
    ProyectoAnualidadIngresosComponent, ProyectoAnualidadIngresoModalComponent,
    ProyectoAnualidadResumenComponent, ProyectoAnualidadEditarComponent],

  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoAnualidadRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ProyectoAnualidadDataResolver
  ]
})
export class ProyectoAnualidadModule { }
