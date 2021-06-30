import { TestBed } from '@angular/core/testing';

import { TipoUnidadGestionService } from './tipo-unidad-gestion.service';

describe('TipoUnidadGestionService', () => {
  let service: TipoUnidadGestionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoUnidadGestionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
