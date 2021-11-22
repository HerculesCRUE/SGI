import { IPaisValidado } from '@core/models/pii/pais-validado';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IPersona } from '@core/models/sgp/persona';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PaisValidadoModalComponent, PaisValidadoModalData } from './pais-validado-modal.component';


describe('InvencionInventorModalComponent', () => {

  let component: PaisValidadoModalComponent;
  let fixture: ComponentFixture<PaisValidadoModalComponent>;

  beforeEach(async () => {

    const data = {
      paisValidado: {
        value: {
          id: 1
        } as IPaisValidado
      },
      paises: []
    } as PaisValidadoModalData;

    await TestBed.configureTestingModule({
      declarations: [PaisValidadoModalComponent],
      imports: [
        SharedModule,
        SgpSharedModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        MatDialogModule,
        FlexModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: data },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaisValidadoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
