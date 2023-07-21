import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { SolicitudProyectoSocioPeriodoJustificacionModalComponent } from './modals/solicitud-proyecto-socio-periodo-justificacion-modal/solicitud-proyecto-socio-periodo-justificacion-modal.component';
import { SolicitudProyectoSocioPeriodoPagoModalComponent } from './modals/solicitud-proyecto-socio-periodo-pago-modal/solicitud-proyecto-socio-periodo-pago-modal.component';
import { SolicitudProyectoSocioCrearComponent } from './solicitud-proyecto-socio-crear/solicitud-proyecto-socio-crear.component';
import { SolicitudProyectoSocioDataResolver } from './solicitud-proyecto-socio-data.resolver';
import { SolicitudProyectoSocioEditarComponent } from './solicitud-proyecto-socio-editar/solicitud-proyecto-socio-editar.component';
import { SolicitudProyectoSocioDatosGeneralesComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.component';
import { SolicitudProyectoSocioEquipoComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-equipo/solicitud-proyecto-socio-equipo.component';
import { SolicitudProyectoSocioPeriodoJustificacionComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion.component';
import { SolicitudProyectoSocioPeriodoPagoComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-pago/solicitud-proyecto-socio-periodo-pago.component';
import { SolicitudProyectoSocioRouting } from './solicitud-proyecto-socio-routing.module';

@NgModule({
  declarations: [
    SolicitudProyectoSocioCrearComponent,
    SolicitudProyectoSocioDatosGeneralesComponent,
    SolicitudProyectoSocioEditarComponent,
    SolicitudProyectoSocioEquipoComponent,
    SolicitudProyectoSocioPeriodoJustificacionComponent,
    SolicitudProyectoSocioPeriodoJustificacionModalComponent,
    SolicitudProyectoSocioPeriodoPagoComponent,
    SolicitudProyectoSocioPeriodoPagoModalComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    SgempSharedModule,
    SgiAuthModule,
    SgpSharedModule,
    SharedModule,
    SolicitudProyectoSocioRouting,
    TranslateModule
  ],
  providers: [
    SolicitudProyectoSocioDataResolver
  ]
})
export class SolicitudProyectoSocioModule { }
