import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../../ejecucion-economica-data.resolver';
import { EjecucionEconomicaActionService, IEjecucionEconomicaData, IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { FacturasEmitidasComponent } from './facturas-emitidas.component';
import { IConfiguracion } from '@core/models/csp/configuracion';

describe('FacturasEmitidasComponent', () => {
  let component: FacturasEmitidasComponent;
  let fixture: ComponentFixture<FacturasEmitidasComponent>;
  const routeData: Data = {
    [EJECUCION_ECONOMICA_DATA_KEY]: {
      proyectoSge: {},
      relaciones: [{ id: 1 } as IRelacionEjecucionEconomicaWithResponsables],
      configuracion: { ejecucionEconomicaGruposEnabled: true } as IConfiguracion,
      readonly: false
    } as IEjecucionEconomicaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FacturasEmitidasComponent],
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
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        EjecucionEconomicaActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FacturasEmitidasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
