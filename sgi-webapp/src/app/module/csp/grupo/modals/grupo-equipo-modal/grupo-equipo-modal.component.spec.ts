import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { GrupoEquipoModalComponent, GrupoEquipoModalData } from './grupo-equipo-modal.component';

describe('GrupoEquipoModalComponent', () => {
  let component: GrupoEquipoModalComponent;
  let fixture: ComponentFixture<GrupoEquipoModalComponent>;

  const newData: GrupoEquipoModalData = {
    titleEntity: 'title',
    selectedEntidades: [],
    entidad: {
      rol: { id: 1 } as IRolProyecto
    } as IGrupoEquipo,
    fechaInicioMin: undefined,
    fechaFinMax: undefined,
    dedicacionMinimaGrupo: undefined,
    grupo: undefined
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        GrupoEquipoModalComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgpSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrupoEquipoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
