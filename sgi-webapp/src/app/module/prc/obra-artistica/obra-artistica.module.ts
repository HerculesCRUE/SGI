import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ObraArtisticaRoutingModule } from './obra-artistica-routing.module';
import { ObraArtisticaListadoComponent } from './obra-artistica-listado/obra-artistica-listado.component';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PrcSharedModule } from '../shared/prc-shared.module';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { ObraArtisticaEditarComponent } from './obra-artistica-editar/obra-artistica-editar.component';
import { ObraArtisticaDatosGeneralesComponent } from './obra-artistica-formulario/obra-artistica-datos-generales/obra-artistica-datos-generales.component';

@NgModule({
  declarations: [ObraArtisticaListadoComponent, ObraArtisticaEditarComponent, ObraArtisticaDatosGeneralesComponent],
  imports: [
    CommonModule,
    SharedModule,
    ObraArtisticaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    CspSharedModule,
    PrcSharedModule,
  ],
  providers: [
    ProduccionCientificaResolver
  ]
})
export class ObraArtisticaModule { }
