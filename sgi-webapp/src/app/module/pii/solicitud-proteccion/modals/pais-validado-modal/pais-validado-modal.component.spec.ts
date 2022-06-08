import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PaisValidadoModalComponent, PaisValidadoModalData } from './pais-validado-modal.component';

describe('PaisValidadoModalComponent', () => {

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
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
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
