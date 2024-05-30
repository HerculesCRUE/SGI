import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoDataResolver } from './proyecto-data.resolver';
import { ProyectoListadoInvComponent } from './proyecto-listado-inv/proyecto-listado-inv.component';
import { ProyectoRoutingInvModule } from './proyecto-routing-inv.module';

@NgModule({
  declarations: [
    ProyectoListadoInvComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    ProyectoRoutingInvModule,
    ReactiveFormsModule,
    SgempSharedModule,
    SgiAuthModule,
    SgpSharedModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    LuxonDatePipe,
    ProyectoDataResolver
  ]
})
export class ProyectoInvModule { }
