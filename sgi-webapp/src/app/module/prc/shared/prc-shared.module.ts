import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { CvnComponentsModule } from './cvn/components/cvn-components.module';
import { CvnPipesModule } from './cvn/pipe/cvn-pipes.module';
import { IndiceImpactoModule } from './indice-impacto/indice-impacto.module';
import { AutorModule } from './autor/autor.module';
import { PrcModalsModule } from './modals/prc-modals.module';



@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    CvnPipesModule,
    CvnComponentsModule,
    IndiceImpactoModule,
    AutorModule,
    PrcModalsModule
  ],
  exports: [
    CvnPipesModule,
    CvnComponentsModule,
    IndiceImpactoModule,
    AutorModule,
    PrcModalsModule
  ]
})
export class PrcSharedModule { }
