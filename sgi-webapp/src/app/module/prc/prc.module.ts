import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PrcInicioComponent } from './prc-inicio/prc-inicio.component';
import { PrcRootComponent } from './prc-root/prc-root.component';
import { PrcRoutingModule } from './prc-routing.module';

@NgModule({
  declarations: [
    PrcRootComponent,
    PrcInicioComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    PrcRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class PrcModule { }
