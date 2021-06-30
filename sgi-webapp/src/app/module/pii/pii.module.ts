import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PiiInicioComponent } from './pii-inicio/pii-inicio.component';
import { PiiMenuPrincipalComponent } from './pii-menu-principal/pii-menu-principal.component';
import { PiiRootComponent } from './pii-root/pii-root.component';
import { PiiRoutingModule } from './pii-routing.module';

@NgModule({
  declarations: [PiiRootComponent, PiiMenuPrincipalComponent, PiiInicioComponent],
  imports: [
    SharedModule,
    CommonModule,
    PiiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: []
})
export class PiiModule { }
