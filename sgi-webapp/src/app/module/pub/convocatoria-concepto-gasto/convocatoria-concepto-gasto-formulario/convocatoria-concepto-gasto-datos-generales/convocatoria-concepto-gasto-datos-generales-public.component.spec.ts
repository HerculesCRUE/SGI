import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from 'src/app/module/csp/shared/csp-shared.module';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from '../../convocatoria-concepto-gasto-public-data.resolver';
import { ConvocatoriaConceptoGastoPublicActionService, IConvocatoriaConceptoGastoPublicData } from '../../convocatoria-concepto-gasto-public.action.service';
import { ConvocatoriaConceptoGastoDatosGeneralesPublicComponent } from './convocatoria-concepto-gasto-datos-generales-public.component';

describe('ConvocatoriaConceptoGastoDatosGeneralesPublicComponent', () => {
  let component: ConvocatoriaConceptoGastoDatosGeneralesPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoDatosGeneralesPublicComponent>;
  const routeData: Data = {
    [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: {
      convocatoria: {
        id: 1
      },
      permitido: true
    } as IConvocatoriaConceptoGastoPublicData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConceptoGastoDatosGeneralesPublicComponent
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
        ConvocatoriaConceptoGastoPublicActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoDatosGeneralesPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
