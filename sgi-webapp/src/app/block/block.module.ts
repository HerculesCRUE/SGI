import { NgModule } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { SelectorModuloComponent } from './selector-modulo/selector-modulo.component';
import { CommonModule } from '@angular/common';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { RouterModule } from '@angular/router';
import { DialogComponent } from './dialog/dialog.component';
import { SnackBarComponent } from './snack-bar/snack-bar.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SgiAuthModule } from '@sgi/framework/auth';

@NgModule({
  declarations: [
    HeaderComponent,
    SelectorModuloComponent,
    DialogComponent,
    SnackBarComponent,
    NavbarComponent
  ],
  imports: [
    CommonModule,
    MaterialDesignModule,
    TranslateModule,
    SharedModule,
    RouterModule,
    SgiAuthModule
  ],
  exports: [
    HeaderComponent,
    NavbarComponent
  ]
})
export class BlockModule { }
