import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcReportService } from '@core/services/prc/report/prc-report.service';
import { PublicacionInvRoutingModule } from './publicacion-inv-routing.module';
import { ProduccionCientificaInvGuard } from '../shared/produccion-cientifica-inv.guard';
import { ProduccionCientificaInvResolver } from '../shared/produccion-cientifica-inv.resolver';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    PublicacionInvRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    PrcSharedModule,
    CspSharedModule
  ],
  providers: [
    ProduccionCientificaInvResolver,
    ProduccionCientificaInvGuard,
    PrcReportService,
    DecimalPipe
  ]
})
export class PublicacionInvModule { }
