import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { SolicitudProteccionCrearComponent } from './solicitud-proteccion-crear/solicitud-proteccion-crear.component';
import { SolicitudProteccionDataResolver } from './solicitud-proteccion-data.resolver';
import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar/solicitud-proteccion-editar.component';
import { SolicitudProteccionDatosGeneralesComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.component';
import { SolicitudProteccionRoutingModule } from './solicitud-proteccion-routing.module';



@NgModule({
  declarations: [
    SolicitudProteccionCrearComponent,
    SolicitudProteccionEditarComponent,
    SolicitudProteccionDatosGeneralesComponent],
  imports: [
    CommonModule,
    SharedModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    SgempSharedModule,
    SgpSharedModule,
    SolicitudProteccionRoutingModule
  ],
  providers: [
    SolicitudProteccionDataResolver
  ]
})
export class SolicitudProteccionModule { }
