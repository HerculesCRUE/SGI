import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SelectInvencionComponent } from './select-invencion/select-invencion.component';
import { SearchInvencionModalComponent } from './select-invencion/dialog/search-invencion.component';
import { SelectTipoProteccionComponent } from './select-tipo-proteccion/select-tipo-proteccion.component';
import { SelectSubtipoProteccionComponent } from './select-subtipo-proteccion/select-subtipo-proteccion.component';
import { SelectSectorAplicacionComponent } from './select-sector-aplicacion/select-sector-aplicacion.component';
import { SelectResultadoInformePatentabilidadComponent } from './select-resultado-informe-patentabilidad/select-resultado-informe-patentabilidad.component';
import { SelectViaProteccionComponent } from './select-via-proteccion/select-via-proteccion.component';
import { SelectTipoProcedimientoComponent } from './select-tipo-procedimiento/select-tipo-procedimiento.component';



@NgModule({
  declarations: [
    SelectInvencionComponent,
    SearchInvencionModalComponent,
    SelectTipoProteccionComponent,
    SelectSubtipoProteccionComponent,
    SelectSectorAplicacionComponent,
    SelectResultadoInformePatentabilidadComponent,
    SelectViaProteccionComponent,
    SelectTipoProcedimientoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    SelectInvencionComponent,
    SelectTipoProteccionComponent,
    SelectSubtipoProteccionComponent,
    SelectSectorAplicacionComponent,
    SelectResultadoInformePatentabilidadComponent,
    SelectViaProteccionComponent,
    SelectTipoProcedimientoComponent
  ]
})
export class PiiSharedModule { }
