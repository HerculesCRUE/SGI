import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IndiceImpactoFuenteImpactoPipe } from './pipe/indice-impacto-fuente-impacto.pipe';
import { IndiceImpactoCuartilPipe } from './pipe/indice-impacto-cuartil.pipe';



@NgModule({
  declarations: [IndiceImpactoFuenteImpactoPipe, IndiceImpactoCuartilPipe],
  imports: [
    CommonModule
  ],
  exports: [IndiceImpactoFuenteImpactoPipe, IndiceImpactoCuartilPipe]
})
export class IndiceImpactoModule { }
