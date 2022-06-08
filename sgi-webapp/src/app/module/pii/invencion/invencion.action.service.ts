import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { ActionService } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InformePatentabilidadService } from '@core/services/pii/informe-patentabilidad/informe-patentabilidad.service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionGastoService } from '@core/services/pii/invencion/invencion-gasto/invencion-gasto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { PeriodoTitularidadService } from '@core/services/pii/invencion/periodo-titularidad/periodo-titularidad.service';
import { SectorLicenciadoService } from '@core/services/pii/sector-licenciado/sector-licenciado.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { GastosInvencionService } from '@core/services/sgepii/gastos-invencion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { map } from 'rxjs/operators';
import { InvencionContratosFragment } from './invencion-formulario/invencion-contratos/invencion-contratos.fragment';
import { InvencionDatosGeneralesFragment } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.fragment';
import { InvencionDocumentoFragment } from './invencion-formulario/invencion-documento/invencion-documento.fragment';
import { InvencionGastosFragment } from './invencion-formulario/invencion-gastos/invencion-gastos.fragment';
import { InvencionInformesPatentabilidadFragment } from './invencion-formulario/invencion-informes-patentabilidad/invencion-informes-patentabilidad.fragment';
import { InvencionIngresosFragment } from './invencion-formulario/invencion-ingresos/invencion-ingresos.fragment';
import { InvencionInventorFragment } from './invencion-formulario/invencion-inventor/invencion-inventor.fragment';
import { InvencionRepartosFragment } from './invencion-formulario/invencion-repartos/invencion-repartos.fragment';
import { PeriodoTitularidadFragment } from './invencion-formulario/periodo-titularidad/periodo-titularidad.fragment';
import { SolicitudProteccionFragment } from './invencion-formulario/solicitud-proteccion/solicitud-proteccion.fragment';
import { INVENCION_ROUTE_PARAMS } from './invencion-route-params';
import { INVENCION_DATA_KEY } from './invencion.resolver';

export interface IInvencionData {
  canEdit: boolean;
  tipoPropiedad: TipoPropiedad;
  invencion: IInvencion;
}

@Injectable()
export class InvencionActionService extends ActionService {

  public readonly id: number;
  private data: IInvencionData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    DOCUMENTOS: 'documentos',
    INFORME_PATENTABILIDAD: 'informe-patentabilidad',
    INVENCION_INVENTOR: 'invencion-inventor',
    SOLICITUDES_PROTECCION: 'solicitudes-proteccion',
    GASTOS: 'gastos',
    INGRESOS: 'ingresos',
    PERIODOS_TITULARIDAD: 'periodos-titularidad',
    CONTRATOS: 'contratos',
    REPARTOS: 'repartos',
  };

  private datosGenerales: InvencionDatosGeneralesFragment;
  private documentos: InvencionDocumentoFragment;
  private informesPatentabilidad: InvencionInformesPatentabilidadFragment;
  private invencionInventoresFragment: InvencionInventorFragment;
  private solicitudesProteccion: SolicitudProteccionFragment;
  private invencionGastos: InvencionGastosFragment;
  private invencionIngresos: InvencionIngresosFragment;
  private periodosTitularidad: PeriodoTitularidadFragment;
  private contratos: InvencionContratosFragment;
  private repartos: InvencionRepartosFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    invencionService: InvencionService,
    invencionDocumentoService: InvencionDocumentoService,
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    documentoService: DocumentoService,
    areaConocimientoService: AreaConocimientoService,
    informePatentabilidadService: InformePatentabilidadService,
    empresaService: EmpresaService,
    personaService: PersonaService,
    readonly logger: NGXLogger,
    solicitudProteccionService: SolicitudProteccionService,
    gastosInvencionService: GastosInvencionService,
    invencionGastosService: InvencionGastoService,
    ingresosInvencionService: IngresosInvencionService,
    private periodoTitularidadService: PeriodoTitularidadService,
    sectorLicenciadoService: SectorLicenciadoService,
    relacionService: RelacionService,
    paisService: PaisService,
    palabraClaveService: PalabraClaveService
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(INVENCION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[INVENCION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new InvencionDatosGeneralesFragment(null, this.id, invencionService, proyectoService, areaConocimientoService,
      invencionDocumentoService, this.canEdit, this.periodoTitularidadService, palabraClaveService);
    this.invencionInventoresFragment = new InvencionInventorFragment(this.id, logger, invencionService,
      personaService, empresaService, this.canEdit);


    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.INVENCION_INVENTOR, this.invencionInventoresFragment);

    if (this.isEdit()) {
      this.documentos = new InvencionDocumentoFragment(this.id, invencionService, invencionDocumentoService, documentoService);
      this.informesPatentabilidad = new InvencionInformesPatentabilidadFragment(
        this.id, this.canEdit, invencionService,
        informePatentabilidadService, documentoService, empresaService
      );

      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
      this.addFragment(this.FRAGMENT.INFORME_PATENTABILIDAD, this.informesPatentabilidad);

      this.solicitudesProteccion = new SolicitudProteccionFragment(this.id, invencionService, solicitudProteccionService,
        this.data.tipoPropiedad, paisService, logger);
      this.addFragment(this.FRAGMENT.SOLICITUDES_PROTECCION, this.solicitudesProteccion);

      this.invencionGastos =
        new InvencionGastosFragment(this.id, gastosInvencionService, invencionGastosService, invencionService, solicitudProteccionService);
      this.addFragment(this.FRAGMENT.GASTOS, this.invencionGastos);

      this.invencionIngresos = new InvencionIngresosFragment(
        this.data.invencion, ingresosInvencionService, invencionService, relacionService, proyectoService);
      this.addFragment(this.FRAGMENT.INGRESOS, this.invencionIngresos);

      this.periodosTitularidad = new PeriodoTitularidadFragment(this.id, logger, periodoTitularidadService, invencionService,
        empresaService);
      this.addFragment(this.FRAGMENT.PERIODOS_TITULARIDAD, this.periodosTitularidad);

      this.contratos = new InvencionContratosFragment(
        this.id, this.canEdit, sectorLicenciadoService, relacionService, proyectoService, empresaService, personaService, paisService);
      this.addFragment(this.FRAGMENT.CONTRATOS, this.contratos);

      this.repartos = new InvencionRepartosFragment(
        this.id, this.canEdit, invencionService);
      this.addFragment(this.FRAGMENT.REPARTOS, this.repartos);
    }
    this.passInventoresFromInventoresFragmentToDatosGeneralesFragmentOnChangesSubscription();
  }

  private passInventoresFromInventoresFragmentToDatosGeneralesFragmentOnChangesSubscription(): void {
    this.subscriptions.push(
      this.invencionInventoresFragment?.invencionInventores$.pipe(
        map((wrappers: StatusWrapper<IInvencionInventor>[]) => wrappers.map(wrapper => wrapper.value))
      ).subscribe(inventores => this.datosGenerales.inventores = inventores));
  }
}
