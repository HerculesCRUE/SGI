import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { NotificacionProyectoExternoCvnService } from './notificacion-proyecto-externo-cvn.service';

describe('NotificacionProyectoExternoCvnService', () => {
  let service: NotificacionProyectoExternoCvnService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(NotificacionProyectoExternoCvnService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
