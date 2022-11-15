import { Component, Input } from '@angular/core';
import { AbstractTableWithoutPaginationComponent } from '@core/component/abstract-table-without-pagination.component';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { IDocumento } from '@core/models/sgdoc/documento';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Rol } from '../evaluacion-formulario.action.service';

@Component({
  selector: 'sgi-evaluacion-listado-anterior-memoria',
  templateUrl: './evaluacion-listado-anterior-memoria.component.html',
  styleUrls: ['./evaluacion-listado-anterior-memoria.component.scss']
})
export class EvaluacionListadoAnteriorMemoriaComponent extends AbstractTableWithoutPaginationComponent<IEvaluacionWithNumComentario> {
  memoriaId: number;
  evaluacionId: number;
  evaluaciones$: Observable<IEvaluacionWithNumComentario[]>;

  @Input()
  rol: Rol;

  constructor(
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService,
    private readonly documentoService: DocumentoService,
    private readonly evaluacionService: EvaluacionService,
  ) {
    super();
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluacionWithNumComentario>> {
    let observable$ = null;
    if (this.memoriaId && this.evaluacionId && this.rol) {
      observable$ = this.memoriaService.getEvaluacionesAnteriores(
        this.memoriaId, this.evaluacionId, this.rol, this.getFindOptions());
    }
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['memoria.numReferencia', 'version', 'fechaDictamen', 'dictamen.nombre', 'numComentarios', 'pdf'];
  }

  protected loadTable(reset?: boolean): void {
    this.evaluaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilters(): SgiRestFilter[] {
    return [];
  }

  /**
   * Visualiza el informe de evaluación seleccionado.
   * @param idEvaluacion id de la memoria del informe
   */
  visualizarInforme(idEvaluacion: number): void {
    const documento: IDocumento = {} as IDocumento;
    this.evaluacionService.getDocumentoEvaluacion(idEvaluacion).pipe(
      switchMap((documentoInfo: IDocumento) => {
        documento.nombre = documentoInfo.nombre;
        return this.documentoService.downloadFichero(documentoInfo.documentoRef);
      })
    ).subscribe(response => {
      triggerDownloadToUser(response, documento.nombre);
    });
  }

}
