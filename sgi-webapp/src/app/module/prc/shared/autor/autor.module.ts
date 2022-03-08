import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AutorNombrePipe } from './pipe/autor-nombre.pipe';
import { AutorApellidosPipe } from './pipe/autor-apellidos.pipe';
import { AutorGrupoEstadoPipe } from './pipe/autor-grupo-estado.pipe';



@NgModule({
  declarations: [AutorNombrePipe, AutorApellidosPipe, AutorGrupoEstadoPipe],
  imports: [
    CommonModule,
  ],
  exports: [AutorNombrePipe, AutorApellidosPipe, AutorGrupoEstadoPipe]
})
export class AutorModule { }
