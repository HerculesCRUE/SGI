import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private appConfig: any;

  constructor(private http: HttpClient) { }

  async loadAppConfig() {
    const data = await this.http.get('./assets/config/config.json')
      .toPromise();
    this.appConfig = data;
  }

  getConfig() {
    return this.appConfig;
  }
}
