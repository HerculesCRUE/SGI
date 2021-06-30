import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import {
  ProyectoPeriodoSeguimientoCrearComponent
} from './proyecto-periodo-seguimiento-crear/proyecto-periodo-seguimiento-crear.component';
import { ProyectoPeriodoSeguimientoDataResolver } from './proyecto-periodo-seguimiento-data.resolver';
import { ProyectoPeriodoSeguimientoEditarComponent } from './proyecto-periodo-seguimiento-editar/proyecto-periodo-seguimiento-editar.component';
import { ProyectoPeriodoSeguimientoDatosGeneralesComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-datos-generales/proyecto-periodo-seguimiento-datos-generales.component';
import { ProyectoPeriodoSeguimientoDocumentosComponent } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-documentos/proyecto-periodo-seguimiento-documentos.component';
import { ProyectoPeriodoSeguimientoRouting } from './proyecto-periodo-seguimiento-routing.module';

@NgModule({
  declarations: [
    ProyectoPeriodoSeguimientoCrearComponent,
    ProyectoPeriodoSeguimientoEditarComponent,
    ProyectoPeriodoSeguimientoDatosGeneralesComponent,
    ProyectoPeriodoSeguimientoDocumentosComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoPeriodoSeguimientoRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ProyectoPeriodoSeguimientoDataResolver
  ]
})
export class ProyectoPeriodoSeguimientoModule { }
