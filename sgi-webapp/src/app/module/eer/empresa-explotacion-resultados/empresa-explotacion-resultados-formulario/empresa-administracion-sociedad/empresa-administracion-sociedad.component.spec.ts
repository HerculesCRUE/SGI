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
import { EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from '../../empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosActionService, IEmpresaExplotacionResultadosData } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaAdministracionSociedadComponent } from './empresa-administracion-sociedad.component';

describe('EmpresaAdministracionSociedadComponent', () => {
  let component: EmpresaAdministracionSociedadComponent;
  let fixture: ComponentFixture<EmpresaAdministracionSociedadComponent>;
  const routeData: Data = {
    [EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY]: {} as IEmpresaExplotacionResultadosData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EmpresaAdministracionSociedadComponent],
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
        EmpresaExplotacionResultadosActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaAdministracionSociedadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

