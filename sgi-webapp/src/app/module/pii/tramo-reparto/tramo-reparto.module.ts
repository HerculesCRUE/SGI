import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TramoRepartoListadoComponent } from './tramo-reparto-listado/tramo-reparto-listado.component';
import { TramoRepartoRoutingModule } from './tramo-reparto-routing.module';
import { TramoRepartoModalComponent } from './tramo-reparto-modal/tramo-reparto-modal.component';
import { TramoRepartoTramoPipe } from './pipes/tramo-reparto-tramo.pipe';



@NgModule({
  declarations: [TramoRepartoListadoComponent, TramoRepartoModalComponent, TramoRepartoTramoPipe],
  imports: [
    CommonModule,
    TramoRepartoRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
  ],
  exports: [
    TramoRepartoTramoPipe
  ],
  providers: [DecimalPipe]
})
export class TramoRepartoModule { }
