import { Injectable } from '@angular/core';
import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IAutorWithGrupos } from '@core/models/prc/autor';
import { ICampoProduccionCientificaWithConfiguracion } from '@core/models/prc/campo-produccion-cientifica';
import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { ProyectoResumenService } from '@core/services/csp/proyecto-resumen/proyecto-resumen.service';
import { AutorService } from '@core/services/prc/autor/autor.service';
import { CampoProduccionCientificaService } from '@core/services/prc/campo-produccion-cientifica/campo-produccion-cientifica.service';
import { ConfiguracionCampoService } from '@core/services/prc/configuracion-campo/configuracion-campo.service';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { from, Observable, of } from 'rxjs';
import { catchError, concatMap, map, mergeMap, toArray } from 'rxjs/operators';
import { PrcModule } from '../prc.module';

@Injectable({
  providedIn: PrcModule
})
export class ProduccionCientificaInitializerService {

  constructor(
    private campoProduccionCientificaService: CampoProduccionCientificaService,
    private configuracionCampo: ConfiguracionCampoService,
    private produccionCientificaService: ProduccionCientificaService,
    private personaService: PersonaService,
    private autorService: AutorService,
    private proyectoResumenService: ProyectoResumenService,
    private documentoService: DocumentoService,
  ) { }

  initializeCamposProduccionCientifica$(produccionCientifica: IProduccionCientifica):
    Observable<Map<string, ICampoProduccionCientificaWithConfiguracion>> {
    return this.campoProduccionCientificaService
      .findAllCamposProduccionCientifca(produccionCientifica)
      .pipe(
        map(camposProduccionCientifica =>
          camposProduccionCientifica.map(
            campo => {
              campo.produccionCientifica = produccionCientifica;
              return campo;
            })),
        mergeMap(camposProduccionCientifica =>
          from(camposProduccionCientifica).pipe(
            mergeMap(campo =>
              this.configuracionCampo.findConfiguracionCampo(campo)
                .pipe(
                  map(configuracion => (
                    {
                      campoProduccionCientifica: campo,
                      configuracionCampo: configuracion
                    } as ICampoProduccionCientificaWithConfiguracion)
                  )
                )
            ),
            toArray()
          )
        ),
        map(camposProduccionCientifica =>
          new Map(camposProduccionCientifica.map(campo => [campo.campoProduccionCientifica.codigo, campo]))
        )
      );
  }

  initializeIndicesImpacto$(produccionCientifica: IProduccionCientifica): Observable<IIndiceImpacto[]> {
    return this.produccionCientificaService.findIndicesImpacto(produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(indiceImpacto => {
          indiceImpacto.produccionCientifica = produccionCientifica;
          return indiceImpacto;
        })),
        catchError(() => of([]))
      );
  }

  initializeAutores$(produccionCientifica: IProduccionCientifica): Observable<IAutorWithGrupos[]> {
    return this.produccionCientificaService.findAutores(produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(autor => {
          autor.produccionCientifica = produccionCientifica;
          return autor;
        })),
        concatMap(autores => from(autores)
          .pipe(
            mergeMap(autor => {
              if (autor.persona?.id) {
                return this.personaService.findById(autor.persona.id)
                  .pipe(
                    map(persona => {
                      autor.persona = persona;
                      return autor;
                    }),
                    catchError(() => of(autor))
                  );
              } else {
                return of(autor);
              }
            }),
            mergeMap(autor => this.autorService.findGrupos(autor.id)
              .pipe(
                map(({ items }) => ({
                  autor,
                  grupos: items
                } as IAutorWithGrupos)),
                catchError(() => of({ autor, grupos: [] }))
              )
            ),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }

  initializeProyectos$(produccionCientifica: IProduccionCientifica): Observable<IProyectoPrc[]> {
    return this.produccionCientificaService.findProyectos(produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(proyectoPrc => {
          proyectoPrc.produccionCientifica = produccionCientifica;
          return proyectoPrc;
        })),
        concatMap(proyectosPrc => from(proyectosPrc)
          .pipe(
            mergeMap(proyectoPrc => this.proyectoResumenService.findById(proyectoPrc?.proyecto?.id)
              .pipe(
                map(proyectoResumen => {
                  proyectoPrc.proyecto = proyectoResumen;
                  return proyectoPrc;
                }),
                catchError(() => of(proyectoPrc)),
              )
            ),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }

  initializeAcreditaciones$(produccionCientifica: IProduccionCientifica): Observable<IAcreditacion[]> {
    return this.produccionCientificaService.findAcreditaciones(produccionCientifica.id)
      .pipe(
        map(({ items }) => items.map(acreditacion => {
          acreditacion.produccionCientifica = produccionCientifica;
          return acreditacion;
        })),
        concatMap(acreditaciones => from(acreditaciones)
          .pipe(
            mergeMap(acreditacion => {
              if (acreditacion.documento?.documentoRef) {
                return this.documentoService.getInfoFichero(acreditacion.documento.documentoRef)
                  .pipe(
                    map(documento => {
                      acreditacion.documento = documento;
                      return acreditacion;
                    }),
                    catchError(() => of(acreditacion))
                  );
              } else {
                return of(acreditacion);
              }
            }),
            toArray()
          )
        ),
        catchError(() => of([]))
      );
  }
}
