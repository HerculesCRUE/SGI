import { NgModule } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { CvnFieldComponent } from './cvn-field/cvn-field.component';
import { CvnEstadoComponent } from './cvn-estado/cvn-estado.component';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { aliasEnumeradosFactory, ALIAS_ENUMERADOS } from '../alias-enumerado.token';
import { AliasEnumeradoService } from '@core/services/prc/alias-enumerado/alias-enumerado.service';
import { CvnPipesModule } from '../pipe/cvn-pipes.module';



@NgModule({
  declarations: [
    CvnFieldComponent,
    CvnEstadoComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    TranslateModule,
    CvnPipesModule
  ],
  exports: [
    CvnFieldComponent,
    CvnEstadoComponent,
  ],
  providers: [
    DecimalPipe,
    {
      provide: ALIAS_ENUMERADOS,
      useFactory: aliasEnumeradosFactory,
      deps: [AliasEnumeradoService]
    }
  ]
})
export class CvnComponentsModule { }
