import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloTipoHitoService } from './modelo-tipo-hito.service';

describe('ModeloTipoHitoService', () => {
  let service: ModeloTipoHitoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloTipoHitoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
