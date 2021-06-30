import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloTipoFaseService } from './modelo-tipo-fase.service';

describe('ModeloTipoFaseService', () => {
  let service: ModeloTipoFaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloTipoFaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

