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
import { AutorizacionCrearComponent } from './autorizacion-crear/autorizacion-crear.component';
import { AutorizacionDataResolver } from './autorizacion-data.resolver';
import { AutorizacionListadoInvComponent } from './autorizacion-listado-inv/autorizacion-listado-inv.component';
import { AutorizacionRoutingInvModule } from './autorizacion-routing-inv.module';

@NgModule({
  declarations: [
    AutorizacionListadoInvComponent,
    AutorizacionCrearComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    AutorizacionRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgempSharedModule,
    SgpSharedModule
  ],
  providers: [
    AutorizacionDataResolver
  ]
})
export class AutorizacionInvModule { }
