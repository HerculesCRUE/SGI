import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { InvencionRoutingModule } from './invencion-routing.module';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';

@NgModule({
  declarations: [InvencionListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    InvencionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class InvencionModule { }
