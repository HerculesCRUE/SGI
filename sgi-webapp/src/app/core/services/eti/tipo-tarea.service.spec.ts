import { TestBed } from '@angular/core/testing';

import { TipoTareaService } from './tipo-tarea.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('TipoTareaService', () => {
  let service: TipoTareaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoTareaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
