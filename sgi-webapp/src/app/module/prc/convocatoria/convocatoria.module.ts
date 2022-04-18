import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { ConvocatoriaBaremacionListadoComponent } from './convocatoria-baremacion-listado/convocatoria-baremacion-listado.component';
import { ConvocatoriaBaremacionCrearComponent } from './convocatoria-baremacion-crear/convocatoria-baremacion-crear.component';
import { ConvocatoriaBaremacionEditarComponent } from './convocatoria-baremacion-editar/convocatoria-baremacion-editar.component';
import { ConvocatoriaBaremacionDatosGeneralesComponent } from './convocatoria-baremacion-formulario/convocatoria-baremacion-datos-generales/convocatoria-baremacion-datos-generales.component';

@NgModule({
  declarations: [
    ConvocatoriaBaremacionListadoComponent,
    ConvocatoriaBaremacionCrearComponent,
    ConvocatoriaBaremacionEditarComponent,
    ConvocatoriaBaremacionDatosGeneralesComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class ConvocatoriaModule { }
