import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SearchInvencionModalComponent } from './select-invencion/dialog/search-invencion.component';
import { SelectInvencionComponent } from './select-invencion/select-invencion.component';
import { SelectResultadoInformePatentabilidadComponent } from './select-resultado-informe-patentabilidad/select-resultado-informe-patentabilidad.component';
import { SelectSectorAplicacionComponent } from './select-sector-aplicacion/select-sector-aplicacion.component';
import { SelectSubtipoProteccionComponent } from './select-subtipo-proteccion/select-subtipo-proteccion.component';
import { SelectTipoProcedimientoComponent } from './select-tipo-procedimiento/select-tipo-procedimiento.component';
import { SelectTipoProteccionComponent } from './select-tipo-proteccion/select-tipo-proteccion.component';
import { SelectViaProteccionComponent } from './select-via-proteccion/select-via-proteccion.component';

@NgModule({
  declarations: [
    SearchInvencionModalComponent,
    SelectInvencionComponent,
    SelectResultadoInformePatentabilidadComponent,
    SelectSectorAplicacionComponent,
    SelectSubtipoProteccionComponent,
    SelectTipoProcedimientoComponent,
    SelectTipoProteccionComponent,
    SelectViaProteccionComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SharedModule,
    TranslateModule
  ],
  exports: [
    SelectInvencionComponent,
    SelectResultadoInformePatentabilidadComponent,
    SelectSectorAplicacionComponent,
    SelectSubtipoProteccionComponent,
    SelectTipoProcedimientoComponent,
    SelectTipoProteccionComponent,
    SelectViaProteccionComponent
  ]
})
export class PiiSharedModule { }
