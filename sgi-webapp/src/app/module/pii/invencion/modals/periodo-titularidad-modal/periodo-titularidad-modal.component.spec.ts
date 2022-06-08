import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
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
