import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PiiSharedModule } from '../shared/pii-shared.module';
import { PaisValidadoModalComponent } from './modals/pais-validado-modal/pais-validado-modal.component';
import { SolicitudProteccionProcedimientoDocumentoModalComponent } from './modals/solicitud-proteccion-procedimiento-documento-modal/solicitud-proteccion-procedimiento-documento-modal.component';
import { SolicitudProteccionProcedimientoModalComponent } from './modals/solicitud-proteccion-procedimiento-modal/solicitud-proteccion-procedimiento-modal.component';
import { SolicitudProteccionCrearComponent } from './solicitud-proteccion-crear/solicitud-proteccion-crear.component';
import { SolicitudProteccionDataResolver } from './solicitud-proteccion-data.resolver';
import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar/solicitud-proteccion-editar.component';
import { SolicitudProteccionDatosGeneralesComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.component';
import { SolicitudProteccionPaisesValidadosComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-paises-validados/solicitud-proteccion-paises-validados.component';
import { SolicitudProteccionProcedimientosDocumentosComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-procedimientos/solicitud-proteccion-procedimientos-documentos/solicitud-proteccion-procedimientos-documentos.component';
import { SolicitudProteccionProcedimientosListadoComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-procedimientos/solicitud-proteccion-procedimientos-listado/solicitud-proteccion-procedimientos-listado.component';
import { SolicitudProteccionProcedimientosComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-procedimientos/solicitud-proteccion-procedimientos.component';
import { SolicitudProteccionRoutingModule } from './solicitud-proteccion-routing.module';



@NgModule({
  declarations: [
    SolicitudProteccionCrearComponent,
    SolicitudProteccionEditarComponent,
    SolicitudProteccionDatosGeneralesComponent,
    SolicitudProteccionPaisesValidadosComponent,
    PaisValidadoModalComponent,
    SolicitudProteccionProcedimientosComponent,
    SolicitudProteccionProcedimientosListadoComponent,
    SolicitudProteccionProcedimientosDocumentosComponent,
    SolicitudProteccionProcedimientoModalComponent,
    SolicitudProteccionProcedimientoDocumentoModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    SgempSharedModule,
    SgpSharedModule,
    SolicitudProteccionRoutingModule,
    PiiSharedModule
  ],
  providers: [
    SolicitudProteccionDataResolver,
  ]
})
export class SolicitudProteccionModule { }
