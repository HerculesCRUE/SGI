import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { SolicitudCrearGuard } from './solicitud-crear/solicitud-crear.guard';
import { SolicitudDataResolver } from './solicitud-data.resolver';
import { SolicitudListadoInvComponent } from './solicitud-listado-inv/solicitud-listado-inv.component';
import { SolicitudRoutingInvModule } from './solicitud-routing-inv.module';

@NgModule({
  declarations: [
    SolicitudListadoInvComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SolicitudRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    CspSharedModule,
    SgiAuthModule,
    SgempSharedModule
  ],
  providers: [
    SolicitudDataResolver,
    SolicitudCrearGuard
  ]
})
export class SolicitudInvModule { }
