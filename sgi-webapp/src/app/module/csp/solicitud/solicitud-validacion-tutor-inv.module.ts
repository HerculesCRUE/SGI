import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { SolicitudDataResolver } from './solicitud-data.resolver';
import { SolicitudValidacionTutorRoutingInvModule } from './solicitud-validacion-tutor-routing-inv.module';
import { ValidacionTutorListadoInvComponent } from './validacion-tutor-listado-inv/validacion-tutor-listado-inv.component';

@NgModule({
  declarations: [
    ValidacionTutorListadoInvComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SolicitudValidacionTutorRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    CspSharedModule,
    SgiAuthModule,
    SgempSharedModule
  ],
  providers: [
    SolicitudDataResolver
  ]
})
export class SolicitudValidacionTutorInvModule { }
