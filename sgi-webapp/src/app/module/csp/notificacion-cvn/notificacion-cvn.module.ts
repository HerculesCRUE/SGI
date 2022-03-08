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
import { NotificacionCvnAsociarAutorizacionModalComponent } from './modals/notificacion-cvn-asociar-autorizacion-modal/notificacion-cvn-asociar-autorizacion-modal.component';
import { NotificacionCvnAsociarProyectoModalComponent } from './modals/notificacion-cvn-asociar-proyecto-modal/notificacion-cvn-asociar-proyecto-modal.component';
import { NotificacionCvnDataResolver } from './notificacion-cvn-data.resolver';
import { NotificacionCvnEditarComponent } from './notificacion-cvn-editar/notificacion-cvn-editar.component';
import { NotificacionCvnDatosGeneralesComponent } from './notificacion-cvn-formulario/notificacion-cvn-datos-generales/notificacion-cvn-datos-generales.component';
import { NotificacionCvnListadoComponent } from './notificacion-cvn-listado/notificacion-cvn-listado.component';
import { NotificacionCvnRoutingModule } from './notificacion-cvn-routing.module';
import { NotificacionCvnEntidadParticipacionPipe } from './pipes/notificacion-cvn-entidad-participacion.pipe';
import { NotificacionCvnResponsablePipe } from './pipes/notificacion-cvn-responsable.pipe';

@NgModule({
  declarations: [
    NotificacionCvnListadoComponent,
    NotificacionCvnEntidadParticipacionPipe,
    NotificacionCvnResponsablePipe,
    NotificacionCvnAsociarAutorizacionModalComponent,
    NotificacionCvnAsociarProyectoModalComponent,
    NotificacionCvnDatosGeneralesComponent,
    NotificacionCvnEditarComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    NotificacionCvnRoutingModule,
    SgpSharedModule,
    SgempSharedModule,
  ],
  providers: [
    NotificacionCvnDataResolver
  ]
})
export class NotificacionCvnModule { }
