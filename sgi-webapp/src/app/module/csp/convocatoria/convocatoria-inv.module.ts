import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ConvocatoriaDataResolver } from './convocatoria-data.resolver';
import { ConvocatoriaListadoInvComponent } from './convocatoria-listado-inv/convocatoria-listado-inv.component';
import { ConvocatoriaRoutingInvModule } from './convocatoria-routing-inv.module';

@NgModule({
  declarations: [
    ConvocatoriaListadoInvComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingInvModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    CspSharedModule,
    SgiAuthModule
  ],
  providers: [
    ConvocatoriaDataResolver
  ]
})
export class ConvocatoriaInvModule { }
