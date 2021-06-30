import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { InvRoutingModule } from './inv-routing.module';
import { InvRootComponent } from './inv-root/inv-root.component';
import { InvMenuPrincipalComponent } from './inv-menu-principal/inv-menu-principal.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { InvInicioComponent } from './inv-inicio/inv-inicio.component';

@NgModule({
  declarations: [InvRootComponent, InvMenuPrincipalComponent, InvInicioComponent],
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
export class InvModule {
}
