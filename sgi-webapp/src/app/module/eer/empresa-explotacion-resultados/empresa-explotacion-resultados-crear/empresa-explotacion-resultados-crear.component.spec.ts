import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from '../empresa-explotacion-resultados-data.resolver';
import { IEmpresaExplotacionResultadosData, EmpresaExplotacionResultadosActionService } from '../empresa-explotacion-resultados.action.service';
import { EmpresaExplotacionResultadosCrearComponent } from './empresa-explotacion-resultados-crear.component';

describe('EmpresaExplotacionResultadosCrearComponent', () => {
  let component: EmpresaExplotacionResultadosCrearComponent;
  let fixture: ComponentFixture<EmpresaExplotacionResultadosCrearComponent>;
  const routeData: Data = {
    [EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY]: {} as IEmpresaExplotacionResultadosData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('0', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EmpresaExplotacionResultadosCrearComponent,
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
        EmpresaExplotacionResultadosActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaExplotacionResultadosCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
