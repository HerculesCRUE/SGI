import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ValidacionClasificacionGastos } from '@core/models/csp/configuracion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../ejecucion-economica-data.resolver';
import { EjecucionEconomicaActionService, IEjecucionEconomicaData, IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar.component';

describe('EjecucionEconomicaEditarComponent', () => {
  let component: EjecucionEconomicaEditarComponent;
  let fixture: ComponentFixture<EjecucionEconomicaEditarComponent>;
  const routeData: Data = {
    [EJECUCION_ECONOMICA_DATA_KEY]: {
      proyectoSge: {},
      relaciones: [{ id: 1 } as IRelacionEjecucionEconomicaWithResponsables],
      readonly: false,
      configuracion: {
        validacionClasificacionGastos: ValidacionClasificacionGastos.ELEGIBILIDAD,
        ejecucionEconomicaGruposEnabled: true
      }
    } as IEjecucionEconomicaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EjecucionEconomicaEditarComponent,
        ActionFooterComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        FlexLayoutModule,
        SharedModule
      ],
      providers: [
        EjecucionEconomicaActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EjecucionEconomicaEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
