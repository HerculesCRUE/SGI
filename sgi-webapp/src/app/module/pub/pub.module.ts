import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PubInicioComponent } from './pub-inicio/pub-inicio.component';
import { PubRootComponent } from './pub-root/pub-root.component';
import { PubRoutingModule } from './pub-routing.module';

@NgModule({
  declarations: [
    PubRootComponent,
    PubInicioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PubRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule
  ],
  providers: []
})
export class PubModule { }
