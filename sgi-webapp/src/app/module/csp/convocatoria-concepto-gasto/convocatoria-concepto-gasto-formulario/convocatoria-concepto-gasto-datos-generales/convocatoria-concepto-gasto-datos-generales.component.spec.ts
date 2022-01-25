import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from '../../convocatoria-concepto-gasto-data.resolver';
import { ConvocatoriaConceptoGastoActionService, IConvocatoriaConceptoGastoData } from '../../convocatoria-concepto-gasto.action.service';
import { ConvocatoriaConceptoGastoDatosGeneralesComponent } from './convocatoria-concepto-gasto-datos-generales.component';

describe('ConvocatoriaConceptoGastoDatosGeneralesComponent', () => {
  let component: ConvocatoriaConceptoGastoDatosGeneralesComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoDatosGeneralesComponent>;
  const routeData: Data = {
    [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: {
      convocatoria: {
        id: 1
      },
      selectedConvocatoriaConceptoGastos: [],
      permitido: true,
      readonly: false
    } as IConvocatoriaConceptoGastoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConceptoGastoDatosGeneralesComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        CspSharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ConvocatoriaConceptoGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
