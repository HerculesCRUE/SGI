import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from 'src/app/module/csp/shared/csp-shared.module';
import { GrupoLineaInvestigacionActionService } from '../../grupo-linea-investigacion.action.service';
import { GrupoLineaInvestigacionDatosGeneralesComponent } from './grupo-linea-investigacion-datos-generales.component';

describe('GrupoLineaInvestigacionDatosGeneralesComponent', () => {
  let component: GrupoLineaInvestigacionDatosGeneralesComponent;
  let fixture: ComponentFixture<GrupoLineaInvestigacionDatosGeneralesComponent>;

  const snapshotData = {
    grupoLineaInvestigacion: {
      lineaInvestigacion: {
        id: 1
      } as ILineaInvestigacion,
    } as IGrupoLineaInvestigacion
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GrupoLineaInvestigacionDatosGeneralesComponent],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        SharedModule,
        CspSharedModule
      ],
      providers: [

        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        GrupoLineaInvestigacionActionService,
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoLineaInvestigacionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
