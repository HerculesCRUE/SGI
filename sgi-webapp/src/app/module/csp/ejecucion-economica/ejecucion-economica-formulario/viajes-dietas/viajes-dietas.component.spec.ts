import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../../ejecucion-economica-data.resolver';
import { EjecucionEconomicaActionService, IEjecucionEconomicaData } from '../../ejecucion-economica.action.service';
import { ViajesDietasComponent } from './viajes-dietas.component';

describe('ViajesDietasComponent', () => {
  let component: ViajesDietasComponent;
  let fixture: ComponentFixture<ViajesDietasComponent>;
  const routeData: Data = {
    [EJECUCION_ECONOMICA_DATA_KEY]: {
      proyectoSge: {},
      proyectosRelacionados: [{ id: 1 } as IProyecto],
      readonly: false
    } as IEjecucionEconomicaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ViajesDietasComponent],
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
    fixture = TestBed.createComponent(ViajesDietasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
