import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoPersonaAutorizadaService } from './grupo-persona-autorizada.service';

describe('GrupoPersonaAutorizadaService', () => {
  let service: GrupoPersonaAutorizadaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoPersonaAutorizadaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
