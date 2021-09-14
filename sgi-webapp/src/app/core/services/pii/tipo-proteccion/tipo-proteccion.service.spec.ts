import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoProteccionService } from './tipo-proteccion.service';

describe('TipoProteccionService', () => {
  let service: TipoProteccionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoProteccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
