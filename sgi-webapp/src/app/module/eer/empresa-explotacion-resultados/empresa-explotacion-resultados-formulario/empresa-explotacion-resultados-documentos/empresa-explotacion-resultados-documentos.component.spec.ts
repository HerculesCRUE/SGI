import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgeSharedModule } from 'src/app/esb/sge/shared/sge-shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from 'src/app/module/csp/shared/csp-shared.module';
import { EerSharedModule } from '../../../shared/eer-shared.module';
import { EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from '../../empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosActionService, IEmpresaExplotacionResultadosData } from '../../empresa-explotacion-resultados.action.service';

import { EmpresaExplotacionResultadosDocumentosComponent } from './empresa-explotacion-resultados-documentos.component';

describe('EmpresaExplotacionResultadosDocumentosComponent', () => {
  let component: EmpresaExplotacionResultadosDocumentosComponent;
  let fixture: ComponentFixture<EmpresaExplotacionResultadosDocumentosComponent>;

  const routeData: Data = {
    [EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY]: {} as IEmpresaExplotacionResultadosData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EmpresaExplotacionResultadosDocumentosComponent
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
        SharedModule,
        SgempSharedModule,
        SgpSharedModule,
        CspSharedModule,
        SgeSharedModule,
        EerSharedModule
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
    fixture = TestBed.createComponent(EmpresaExplotacionResultadosDocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
