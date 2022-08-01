import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaExplotacionResultadosDatosGeneralesComponent } from './empresa-explotacion-resultados-datos-generales.component';

describe('EmpresaExplotacionResultadosDatosGeneralesComponent', () => {
  let component: EmpresaExplotacionResultadosDatosGeneralesComponent;
  let fixture: ComponentFixture<EmpresaExplotacionResultadosDatosGeneralesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EmpresaExplotacionResultadosDatosGeneralesComponent
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
        SgeSharedModule
      ],
      providers: [
        EmpresaExplotacionResultadosActionService,
        SgiAuthService,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaExplotacionResultadosDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
