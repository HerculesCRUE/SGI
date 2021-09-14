import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { InvInicioComponent } from './inv-inicio/inv-inicio.component';
import { InvRootComponent } from './inv-root/inv-root.component';
import { InvRoutingModule } from './inv-routing.module';

@NgModule({
  declarations: [
    InvRootComponent,
    InvInicioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    InvRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  providers: []
})
export class InvModule { }
