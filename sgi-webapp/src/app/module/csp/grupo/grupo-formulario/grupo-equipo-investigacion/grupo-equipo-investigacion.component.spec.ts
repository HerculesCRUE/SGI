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
import { GRUPO_DATA_KEY } from '../../grupo-data.resolver';
import { GrupoActionService, IGrupoData } from '../../grupo.action.service';
import { GrupoEquipoInvestigacionExportService } from './grupo-equipo-investigacion-export.service';
import { GrupoEquipoInvestigacionComponent } from './grupo-equipo-investigacion.component';

describe('GrupoEquipoInvestigacionComponent', () => {
  let component: GrupoEquipoInvestigacionComponent;
  let fixture: ComponentFixture<GrupoEquipoInvestigacionComponent>;
  const routeData: Data = {
    [GRUPO_DATA_KEY]: {} as IGrupoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GrupoEquipoInvestigacionComponent],
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
        GrupoActionService,
        SgiAuthService,
        GrupoEquipoInvestigacionExportService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoEquipoInvestigacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

