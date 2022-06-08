import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { BaremoPesoCuantiaComponent } from './components/baremo-peso-cuantia/baremo-peso-cuantia.component';
import { BaremoPesoPuntosComponent } from './components/baremo-peso-puntos/baremo-peso-puntos.component';
import { BaremoPesoComponent } from './components/baremo-peso/baremo-peso.component';
import { BaremoPuntosComponent } from './components/baremo-puntos/baremo-puntos.component';
import { BaremoDirective } from './components/baremo.directive';
import { ConvocatoriaBaremacionCrearComponent } from './convocatoria-baremacion-crear/convocatoria-baremacion-crear.component';
import { ConvocatoriaBaremacionEditarComponent } from './convocatoria-baremacion-editar/convocatoria-baremacion-editar.component';
import { ConvocatoriaBaremacionBaremosPuntuacionesComponent } from './convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.component';
import { ConvocatoriaBaremacionDatosGeneralesComponent } from './convocatoria-baremacion-formulario/convocatoria-baremacion-datos-generales/convocatoria-baremacion-datos-generales.component';
import { ModuladoresRangosComponent } from './convocatoria-baremacion-formulario/moduladores-rangos/moduladores-rangos.component';
import { ConvocatoriaBaremacionListadoComponent } from './convocatoria-baremacion-listado/convocatoria-baremacion-listado.component';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { RangoModalComponent } from './modals/rango-modal/rango-modal.component';
import { RangoPipe } from './pipes/rango.pipe';

@NgModule({
  declarations: [
    ConvocatoriaBaremacionListadoComponent,
    ConvocatoriaBaremacionCrearComponent,
    ConvocatoriaBaremacionEditarComponent,
    ConvocatoriaBaremacionDatosGeneralesComponent,
    ConvocatoriaBaremacionBaremosPuntuacionesComponent,
    BaremoPesoPuntosComponent,
    BaremoPesoCuantiaComponent,
    BaremoPesoComponent,
    BaremoPuntosComponent,
    BaremoDirective,
    ModuladoresRangosComponent,
    RangoModalComponent,
    RangoPipe
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
  ],
  providers: [
    DecimalPipe
  ]
})
export class ConvocatoriaModule { }
