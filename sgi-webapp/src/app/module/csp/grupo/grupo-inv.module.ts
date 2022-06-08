import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { GrupoDataResolver } from './grupo-data.resolver';
import { GrupoListadoInvComponent } from './grupo-listado-inv/grupo-listado-inv.component';
import { GrupoRoutingInvModule } from './grupo-routing-inv.module';

@NgModule({
  declarations: [
    GrupoListadoInvComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    GrupoRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    CspSharedModule,
    SgiAuthModule,
    SgpSharedModule
  ],
  providers: [
    GrupoDataResolver
  ]
})
export class GrupoInvModule { }
