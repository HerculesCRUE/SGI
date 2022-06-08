import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PrcReportService } from '@core/services/prc/report/prc-report.service';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PersonaNombreCompletoPipe } from 'src/app/esb/sgp/shared/pipes/persona-nombre-completo.pipe';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { InformeGenerarComponent } from './informe-generar/informe-generar.component';
import { InformeRoutingModule } from './informe-routing.module';

@NgModule({
  declarations: [
    InformeGenerarComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    InformeRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule
  ],
  providers: [
    PrcReportService,
    DecimalPipe,
    PersonaNombreCompletoPipe
  ]
})
export class InformeModule { }
