import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PublicacionRoutingModule } from './publicacion-routing.module';
import { PublicacionListadoComponent } from './publicacion-listado/publicacion-listado.component';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PublicacionDatosGeneralesComponent } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.component';
import { PublicacionEditarComponent } from './publicacion-editar/publicacion-editar.component';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcReportService } from '@core/services/prc/report/prc-report.service';

@NgModule({
  declarations: [PublicacionListadoComponent, PublicacionDatosGeneralesComponent, PublicacionEditarComponent],
  imports: [
    CommonModule,
    SharedModule,
    PublicacionRoutingModule,
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
    ProduccionCientificaResolver,
    PrcReportService,
    DecimalPipe
  ]
})
export class PublicacionModule { }
