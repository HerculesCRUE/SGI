import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ClasificacionPublicModalComponent } from './clasificacion-public-modal/clasificacion-public-modal.component';
import { SelectComunidadAutonomaPublicComponent } from './select-comunidad-autonoma-public/select-comunidad-autonoma-public.component';
import { SelectPaisPublicComponent } from './select-pais-public/select-pais-public.component';
import { SelecProvinciaPublicComponent } from './select-provincia-public/select-provincia-public.component';

@NgModule({
  declarations: [
    SelectComunidadAutonomaPublicComponent,
    SelecProvinciaPublicComponent,
    SelectPaisPublicComponent,
    ClasificacionPublicModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    SelectComunidadAutonomaPublicComponent,
    SelecProvinciaPublicComponent,
    SelectPaisPublicComponent,
    ClasificacionPublicModalComponent
  ]
})
export class SgoPublicSharedModule { }
