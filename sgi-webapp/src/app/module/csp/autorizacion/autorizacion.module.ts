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
import { AutorizacionDataResolver } from './autorizacion-data.resolver';
import { AutorizacionEditarComponent } from './autorizacion-editar/autorizacion-editar.component';
import { AutorizacionCertificadoModalComponent } from './autorizacion-formulario/autorizacion-certificado-modal/autorizacion-certificado-modal.component';
import { AutorizacionCertificadosComponent } from './autorizacion-formulario/autorizacion-certificados/autorizacion-certificados.component';
import { AutorizacionDatosGeneralesComponent } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.component';
import { AutorizacionHistoricoEstadosComponent } from './autorizacion-formulario/autorizacion-historico-estados/autorizacion-historico-estados.component';
import { AutorizacionListadoComponent } from './autorizacion-listado/autorizacion-listado.component';
import { AutorizacionRoutingModule } from './autorizacion-routing.module';
import { CambioEstadoModalComponent } from './cambio-estado-modal/cambio-estado-modal.component';

@NgModule({
  declarations: [
    AutorizacionListadoComponent,
    AutorizacionDatosGeneralesComponent,
    AutorizacionEditarComponent,
    CambioEstadoModalComponent,
    AutorizacionHistoricoEstadosComponent,
    AutorizacionCertificadosComponent,
    AutorizacionCertificadoModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    AutorizacionRoutingModule,
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
export class AutorizacionModule { }
