import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IMiembroEquipoProyecto } from '@core/models/csp/miembro-equipo-proyecto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MiembroEquipoProyectoModalComponent, MiembroEquipoProyectoModalData } from './miembro-equipo-proyecto-modal.component';

describe('MiembroEquipoProyectoModalComponent', () => {
  let component: MiembroEquipoProyectoModalComponent;
  let fixture: ComponentFixture<MiembroEquipoProyectoModalComponent>;

  const newData: MiembroEquipoProyectoModalData = {
    titleEntity: 'title',
    isEdit: false,
    selectedEntidades: [],
    entidad: {} as IMiembroEquipoProyecto,
    fechaFinMax: undefined,
    fechaInicioMin: undefined,
    showHorasDedicacion: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        MiembroEquipoProyectoModalComponent
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
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: newData },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MiembroEquipoProyectoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
