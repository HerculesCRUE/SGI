import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacionCvnListadoComponent } from './notificacion-cvn-listado/notificacion-cvn-listado.component';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { CspSharedModule } from '../shared/csp-shared.module';
import { NotificacionCvnRoutingModule } from './notificacion-cvn-routing.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { NotificacionCvnEntidadParticipacionPipe } from './pipes/notificacion-cvn-entidad-participacion.pipe';
import { NotificacionCvnResponsablePipe } from './pipes/notificacion-cvn-responsable.pipe';

@NgModule({
  declarations: [NotificacionCvnListadoComponent, NotificacionCvnEntidadParticipacionPipe, NotificacionCvnResponsablePipe],
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
  ]
})
export class NotificacionCvnModule { }
