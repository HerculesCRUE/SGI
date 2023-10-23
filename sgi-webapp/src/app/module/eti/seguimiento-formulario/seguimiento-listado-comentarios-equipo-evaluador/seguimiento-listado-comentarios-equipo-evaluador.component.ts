import { Component, Input } from '@angular/core';
import { AbstractTableWithoutPaginationComponent } from '@core/component/abstract-table-without-pagination.component';
import { TipoEstadoComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { Rol } from '../seguimiento-formulario.action.service';

export interface IEvaluadorWithComentariosAndEnviados extends IEvaluador {
  numComentarios: number;
  evaluador1: boolean;
  evaluador2: boolean;
  comentariosEnviados: boolean;
}

@Component({
  selector: 'sgi-seguimiento-listado-comentarios-equipo-evaluador',
  templateUrl: './seguimiento-listado-comentarios-equipo-evaluador.component.html',
  styleUrls: ['./seguimiento-listado-comentarios-equipo-evaluador.component.scss']
})
export class SeguimientoListadoComentariosEquipoEvaluadorComponent extends AbstractTableWithoutPaginationComponent<IEvaluadorWithComentariosAndEnviados> {
  evaluadores$: Observable<IEvaluadorWithComentariosAndEnviados[]>;

  @Input()
  rol: Rol;

  @Input()
  evaluacion: IEvaluacion;

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly personaService: PersonaService,
    private readonly evaluadorService: EvaluadorService,
    private readonly evaluacionService: EvaluacionService
  ) {
    super();
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluadorWithComentariosAndEnviados>> {
    return this.evaluadorService.findAllMemoriasAsignablesConvocatoria(this.evaluacion?.convocatoriaReunion.comite.id, this.evaluacion?.memoria.id, this.evaluacion?.convocatoriaReunion.fechaEvaluacion).pipe(
      map(response => {
        return response as SgiRestListResult<IEvaluadorWithComentariosAndEnviados>;
      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(evaluador => {
            if (evaluador?.persona.id) {
              return this.personaService.findById(evaluador.persona.id).pipe(
                map(persona => {
                  evaluador.persona = persona;
                  evaluador.evaluador1 = evaluador.id === this.evaluacion.evaluador1.id;
                  evaluador.evaluador2 = evaluador.id === this.evaluacion.evaluador2.id;

                  evaluador.numComentarios = 0;
                  evaluador.comentariosEnviados = false;
                  return evaluador;
                })
              );
            }
            return of(evaluador);
          }),
          mergeMap(evaluador => {
            if (evaluador?.persona.id) {
              return this.evaluacionService.getComentariosPersonaEvaluador(this.evaluacion.id, evaluador.persona.id).pipe(
                map(comentariosEvaluador => {
                  evaluador.numComentarios = comentariosEvaluador.length;
                  evaluador.comentariosEnviados = comentariosEvaluador.some(comentario => comentario.estado === TipoEstadoComentario.CERRADO);
                })
              );
            }
            return of(evaluador);
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      ));
  }

  protected initColumns(): void {
    this.columnas = ['persona.nombre', 'rolEvaluacion', 'numComentarios', 'comentariosEnviados'];
  }

  protected loadTable(reset?: boolean): void {
    this.evaluadores$ = this.getObservableLoadTable(reset);
  }

  protected createFilters(): SgiRestFilter[] {
    return [];
  }


}
