import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoResponsableEconomicoModalComponent, SolicitudProyectoResponsableEconomicoModalData } from './solicitud-proyecto-responsable-economico-modal.component';

describe('SolicitudProyectoResponsableEconomicoModalComponent', () => {
  let component: SolicitudProyectoResponsableEconomicoModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoResponsableEconomicoModalComponent>;

  const data: SolicitudProyectoResponsableEconomicoModalData = {
    selectedEntidades: [],
    entidad: {} as ISolicitudProyectoResponsableEconomico,
    mesMax: 20,
    isEdit: true,
    readonly: true
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoResponsableEconomicoModalComponent,
        DialogComponent,
        HeaderComponent
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
        { provide: MatDialogRef, useValue: data },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoResponsableEconomicoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
