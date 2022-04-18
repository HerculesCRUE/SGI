import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { Observable, of } from 'rxjs';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { DesgloseEconomicoFragment, IColumnDefinition, RowTreeDesglose } from './desglose-economico.fragment';

export abstract class EjecucionPresupuestariaFragment extends DesgloseEconomicoFragment<IDatoEconomico> {

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService);
    this.setComplete(true);
  }

  protected abstract getColumns(): Observable<IColumnDefinition[]>;

  protected abstract getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]>;

  protected buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<IDatoEconomico>[]> {
    const root: RowTreeDesglose<IDatoEconomico>[] = [];
    const mapTree = new Map<string, RowTreeDesglose<IDatoEconomico>>();
    datosEconomicos.forEach(element => {
      const keyAnualidad = `${element.anualidad}-${element.tipo}`;
      const keyPartida = `${keyAnualidad}-${element.partidaPresupuestaria}`;
      let anualidad = mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreeDesglose(
          {
            anualidad: element.anualidad,
            tipo: element.tipo,
            partidaPresupuestaria: '',
            columnas: this.processColumnsValues(element.columnas, this.columns, true)
          } as IDatoEconomico
        );
        mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }
      let partida = mapTree.get(keyPartida);
      if (!partida) {
        partida = new RowTreeDesglose(
          {
            anualidad: '',
            tipo: '',
            partidaPresupuestaria: element.partidaPresupuestaria,
            columnas: this.processColumnsValues(element.columnas, this.columns, true)
          } as IDatoEconomico
        );
        mapTree.set(keyPartida, partida);
        anualidad.addChild(partida);
      }
      partida.addChild(new RowTreeDesglose(
        {
          anualidad: element.anualidad,
          tipo: element.tipo,
          partidaPresupuestaria: element.partidaPresupuestaria,
          columnas: this.processColumnsValues(element.columnas, this.columns, false)
        } as IDatoEconomico
      ));
    });
    return of(root);
  }
}
