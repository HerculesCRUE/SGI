import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoEnlaceRoutingModule } from './tipo-enlace-routing.module';
import { TipoEnlaceListadoComponent } from './tipo-enlace-listado/tipo-enlace-listado.component';
import { TipoEnlaceModalComponent } from './tipo-enlace-modal/tipo-enlace-modal.component';

@NgModule({
  declarations: [
    TipoEnlaceListadoComponent,
    TipoEnlaceModalComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    TipoEnlaceRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class TipoEnlaceModule { }
