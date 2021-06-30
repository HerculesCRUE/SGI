import { CommonModule } from '@angular/common';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { EtiRoutingModule } from './eti-routing.module';
import { EtiRootComponent } from './eti-root/eti-root.component';
import { EtiMenuPrincipalComponent } from './eti-menu-principal/eti-menu-principal.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { EtiInicioComponent } from './eti-inicio/eti-inicio.component';

@NgModule({
  declarations: [EtiRootComponent, EtiMenuPrincipalComponent, EtiInicioComponent],
  imports: [
    SharedModule,
    CommonModule,
    EtiRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: []
})
export class EtiModule {
}
