import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoMemoriaService } from './tipo-memoria.service';

describe('TipoMemoriaService', () => {
  let service: TipoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
