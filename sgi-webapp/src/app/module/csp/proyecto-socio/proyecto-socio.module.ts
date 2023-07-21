import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoSocioPeriodoPagoModalComponent } from './modals/proyecto-socio-periodo-pago-modal/proyecto-socio-periodo-pago-modal.component';
import { ProyectoSocioCrearComponent } from './proyecto-socio-crear/proyecto-socio-crear.component';
import { ProyectoSocioDataResolver } from './proyecto-socio-data.resolver';
import { ProyectoSocioEditarComponent } from './proyecto-socio-editar/proyecto-socio-editar.component';
import { ProyectoSocioDatosGeneralesComponent } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.component';
import { ProyectoSocioEquipoComponent } from './proyecto-socio-formulario/proyecto-socio-equipo/proyecto-socio-equipo.component';
import { ProyectoSocioPeriodoJustificacionComponent } from './proyecto-socio-formulario/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.component';
import { ProyectoSocioPeriodoPagoComponent } from './proyecto-socio-formulario/proyecto-socio-periodo-pago/proyecto-socio-periodo-pago.component';
import { ProyectoSocioRouting } from './proyecto-socio-routing.module';

@NgModule({
  declarations: [
    ProyectoSocioCrearComponent,
    ProyectoSocioDatosGeneralesComponent,
    ProyectoSocioEditarComponent,
    ProyectoSocioEquipoComponent,
    ProyectoSocioPeriodoJustificacionComponent,
    ProyectoSocioPeriodoPagoComponent,
    ProyectoSocioPeriodoPagoModalComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    ProyectoSocioRouting,
    ReactiveFormsModule,
    SgempSharedModule,
    SgiAuthModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    ProyectoSocioDataResolver
  ]
})
export class ProyectoSocioModule { }
