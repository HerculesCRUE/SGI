import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { IPeriodoTitularidadModalData, PeriodoTitularidadModalComponent } from './periodo-titularidad-modal.component';

describe('PeriodoTitularidadModalComponent', () => {
  let component: PeriodoTitularidadModalComponent;
  let fixture: ComponentFixture<PeriodoTitularidadModalComponent>;

  const data = {
    periodoTitularidad: {
      value: {}
    }
  } as IPeriodoTitularidadModalData;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeriodoTitularidadModalComponent],
      imports: [
        TestUtils.getIdiomas(),
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
    fixture = TestBed.createComponent(PeriodoTitularidadModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
