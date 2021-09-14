import { Component, Input } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTableWithoutPaginationComponent } from '@core/component/abstract-table-without-pagination.component';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IInforme } from '@core/models/eti/informe';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ERROR = marker('error.load');
const FICHA_EVALUADOR = marker('eti.documentacion-memoria.ficha-evaluador');

interface IDocumentacionMemoriaWithInformeAndFichaEvaluador extends IDocumentacionMemoria {
  informe: IInforme;
  fichaEvaluador: string;
}

@Component({
  selector: 'sgi-documentacion-memoria-listado-memoria',
  templateUrl: './documentacion-memoria-listado-memoria.component.html',
  styleUrls: ['./documentacion-memoria-listado-memoria.component.scss']
})

export class DocumentacionMemoriaListadoMemoriaComponent extends
  AbstractTableWithoutPaginationComponent<IDocumentacionMemoriaWithInformeAndFichaEvaluador>  {
  documentacionMemoria$: Observable<IDocumentacionMemoriaWithInformeAndFichaEvaluador[]>;
  @Input() memoriaId: number;
  @Input() tipoEvaluacion: number;
  @Input() fichaEvaluador: boolean;

  constructor(
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(snackBarService, MSG_ERROR);
  }

  protected createObservable(): Observable<SgiRestListResult<IDocumentacionMemoriaWithInformeAndFichaEvaluador>> {
    const observable$ = this.memoriaService.getDocumentacionesTipoEvaluacion(this.memoriaId, this.tipoEvaluacion, this.getFindOptions()).
      pipe(
        map(response => {
          return response;
        }),
        switchMap(response => {
          return this.memoriaService.findInformeUltimaVersion(this.memoriaId).pipe(
            switchMap(res => {
              const documentacionMemoria: IDocumentacionMemoriaWithInformeAndFichaEvaluador[] = [];
              const documentoInforme = {
                informe: res
              } as IDocumentacionMemoriaWithInformeAndFichaEvaluador;
              documentacionMemoria.push(documentoInforme);
              if (this.fichaEvaluador) {
                const documentoFichaEvaluador = {
                  fichaEvaluador: FICHA_EVALUADOR
                } as IDocumentacionMemoriaWithInformeAndFichaEvaluador;
                documentacionMemoria.push(documentoFichaEvaluador);
              }
              response.items.map(documentoMemoria =>
                documentacionMemoria.push(documentoMemoria as IDocumentacionMemoriaWithInformeAndFichaEvaluador));
              return of({
                items: documentacionMemoria,
                page: response.page,
                total: response.total
              } as SgiRestListResult<IDocumentacionMemoriaWithInformeAndFichaEvaluador>);
            })
          );
        })
      );
    return observable$;
  }

  protected getObservableLoadTable(reset?: boolean): Observable<IDocumentacionMemoriaWithInformeAndFichaEvaluador[]> {
    if (this.memoriaId) {
      return super.getObservableLoadTable(reset);
    }
    return null;
  }

  protected initColumns(): void {
    this.columnas = ['documentoRef', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.documentacionMemoria$ = this.getObservableLoadTable(reset);
  }

  protected createFilters(): SgiRestFilter[] {
    return [];
  }

  generarInforme(informe: IInforme) {
    // TODO generar informe
  }

  generarFichaEvaluador(documentacion: IDocumentacionMemoriaWithInformeAndFichaEvaluador) {
    // TODO generar ficha evaluador
  }

  generarDocumentacionMemoria(documentacion: IDocumentacionMemoriaWithInformeAndFichaEvaluador) {
    // TODO generar documentacion
  }

  private getVersion(informe: IInforme) {
    switch (informe.tipoEvaluacion.id) {
      case TIPO_EVALUACION.MEMORIA:
        return informe.memoria?.numReferencia + '_v' + informe.version;
      case TIPO_EVALUACION.RETROSPECTIVA:
        return informe.memoria?.numReferencia + '_R_v' + informe.version;
      case TIPO_EVALUACION.SEGUIMIENTO_ANUAL:
        return informe.memoria?.numReferencia + '_SA_v' + informe.version;
      case TIPO_EVALUACION.SEGUIMIENTO_FINAL:
        return informe.memoria?.numReferencia + '_SF_v' + informe.version;
      default:
        return '';
    }
  }

  getNombre(documento: IDocumentacionMemoriaWithInformeAndFichaEvaluador) {
    if (documento.informe) {
      return this.getVersion(documento.informe);
    }
    switch (this.tipoEvaluacion) {
      case TIPO_EVALUACION.MEMORIA:
        return `${documento.tipoDocumento.nombre}: ${documento.nombre}`;
      default:
        return documento.nombre;
    }
  }
}