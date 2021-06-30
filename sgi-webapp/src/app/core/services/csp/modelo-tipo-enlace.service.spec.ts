import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloTipoEnlaceService } from './modelo-tipo-enlace.service';

describe('ModeloTipoEnlaceService', () => {
  let service: ModeloTipoEnlaceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloTipoEnlaceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
