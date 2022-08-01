import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { EerInicioComponent } from './eer-inicio/eer-inicio.component';
import { EerRootComponent } from './eer-root/eer-root.component';
import { EerRoutingModule } from './eer-routing.module';

@NgModule({
  declarations: [
    EerRootComponent,
    EerInicioComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    EerRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class EerModule { }
