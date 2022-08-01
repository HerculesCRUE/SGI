import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { ProduccionCientificaInvGuard } from '../shared/produccion-cientifica-inv.guard';
import { ProduccionCientificaInvResolver } from '../shared/produccion-cientifica-inv.resolver';
import { ComiteEditorialInvRoutingModule } from './comite-editorial-inv-routing.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    ComiteEditorialInvRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    CspSharedModule,
    PrcSharedModule
  ],
  providers: [
    ProduccionCientificaInvResolver,
    ProduccionCientificaInvGuard,
  ]
})
export class ComiteEditorialInvModule { }
