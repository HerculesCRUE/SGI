import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { AreaConocimientoModalComponent } from './area-conocimiento-modal/area-conocimiento-modal.component';
import { ClasificacionModalComponent } from './clasificacion-modal/clasificacion-modal.component';

@NgModule({
  declarations: [
    AreaConocimientoModalComponent,
    ClasificacionModalComponent
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
    AreaConocimientoModalComponent,
    ClasificacionModalComponent
  ]
})
export class SgoSharedModule { }
