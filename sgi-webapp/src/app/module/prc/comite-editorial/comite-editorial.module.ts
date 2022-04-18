import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ComiteEditorialRoutingModule } from './comite-editorial-routing.module';
import { ComiteEditorialListadoComponent } from './comite-editorial-listado/comite-editorial-listado.component';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { ComiteEditorialEditarComponent } from './comite-editorial-editar/comite-editorial-editar.component';
import { ComiteEditorialDatosGeneralesComponent } from './comite-editorial-formulario/comite-editorial-datos-generales/comite-editorial-datos-generales.component';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { PrcSharedModule } from '../shared/prc-shared.module';

@NgModule({
  declarations: [ComiteEditorialListadoComponent, ComiteEditorialEditarComponent, ComiteEditorialDatosGeneralesComponent],
  imports: [
    CommonModule,
    SharedModule,
    ComiteEditorialRoutingModule,
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
    ProduccionCientificaResolver
  ]
})
export class ComiteEditorialModule { }
