import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { InvencionCrearComponent } from './invencion-crear/invencion-crear.component';
import { InvencionEditarComponent } from './invencion-editar/invencion-editar.component';
import { InvencionDatosGeneralesComponent } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.component';
import { InvencionInventorComponent } from './invencion-formulario/invencion-inventor/invencion-inventor.component';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';
import { InvencionRoutingModule } from './invencion-routing.module';
import { InvencionResolver } from './invencion.resolver';
import { InvencionInventorModalComponent } from './modals/invencion-inventor-modal/invencion-inventor-modal.component';
import { SectorAplicacionModalComponent } from './modals/sector-aplicacion-modal/sector-aplicacion-modal.component';
import { InvencionDocumentoComponent } from './invencion-formulario/invencion-documento/invencion-documento.component';
import { InvencionDocumentoModalComponent } from './modals/invencion-documento-modal/invencion-documento-modal.component';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { InvencionInformesPatentabilidadComponent } from './invencion-formulario/invencion-informes-patentabilidad/invencion-informes-patentabilidad.component';
import { InformePatentabilidadModalComponent } from './modals/informe-patentabilidad-modal/informe-patentabilidad-modal.component';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SolicitudProteccionComponent } from './invencion-formulario/solicitud-proteccion/solicitud-proteccion.component';
import { InvencionGastosComponent } from './invencion-formulario/invencion-gastos/invencion-gastos.component';

@NgModule({
  declarations: [
    InvencionListadoComponent,
    InvencionCrearComponent,
    InvencionDatosGeneralesComponent,
    InvencionEditarComponent,
    SectorAplicacionModalComponent,
    InvencionDocumentoComponent,
    InvencionDocumentoModalComponent,
    InvencionInformesPatentabilidadComponent,
    InformePatentabilidadModalComponent,
    InvencionInventorComponent,
    InvencionInventorModalComponent,
    SolicitudProteccionComponent,
    InvencionGastosComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    InvencionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule,
    SgoSharedModule,
    SgempSharedModule
  ],
  providers: [
    InvencionResolver
  ]
})
export class InvencionModule { }
