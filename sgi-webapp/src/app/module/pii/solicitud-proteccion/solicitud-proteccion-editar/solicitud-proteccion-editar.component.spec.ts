import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar.component';

describe('SolicitudProteccionEditarComponent', () => {
  let component: SolicitudProteccionEditarComponent;
  let fixture: ComponentFixture<SolicitudProteccionEditarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SolicitudProteccionEditarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
