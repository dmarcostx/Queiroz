import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
import {OidcSecurityService} from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root', templateUrl: './app.component.html', styleUrls: ['./app.component.css'], standalone: false
})
export class AppComponent implements OnInit {
  title = 'frontend';
  isAuthenticated = false;
  entities: any[] = [];
  userData: any = null;
  users: any = null;
  disciplines: any = [];
  newDisciplineTitle: string = "";
  newCourseTitle: string = "";
  newSemesterNumber: number = 0;
  loading = false;
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly http = inject(HttpClient);
  private backendApiUrl = 'http://localhost:8080';
  private kcUrl = 'http://keycloak:8180/realms/quarkus-app-realm/users';

  ngOnInit() {
    this.oidcSecurityService.checkAuth().subscribe(({isAuthenticated, userData}) => {
      this.isAuthenticated = isAuthenticated;
      this.userData = userData;
      if (isAuthenticated) {
        this.load();
        this.openCreateUser();
      } else this.oidcSecurityService.authorize();
    });
  }

  load() {
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        if (this.hasRole('coordenador') || this.hasRole('professor') || this.hasRole('aluno')) this.http.get(`${this.backendApiUrl}/discipline`, {
          headers, responseType: 'json'
        }).subscribe({
          next: (response) => this.disciplines = response
        });
      }
    });
  }

  logout() {
    this.oidcSecurityService.logoffAndRevokeTokens().subscribe(() => {
      this.oidcSecurityService.logoffLocal();
    });
  }

  hasRole(role: string) {
    return this.userData && this.userData.roles && this.userData.roles.includes(role);
  }

  createDiscipline() {
    this.loading = true;
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.post(`${this.backendApiUrl}/discipline`, {
          name: this.newDisciplineTitle.trim(), semester: this.newSemesterNumber, course: this.newCourseTitle.trim()
        }, {headers, responseType: 'json'}).subscribe(() => {
          this.newCourseTitle = "";
          this.newSemesterNumber = 0;
          this.newDisciplineTitle = "";
          this.loading = false;
          this.load()
        });
      }
    });
  }

  deleteDiscipline(discipline: any) {
    this.oidcSecurityService.getAccessToken().subscribe((token) => {
      if (token) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        this.http.delete(`${this.backendApiUrl}/discipline`, {
          headers, responseType: 'json', body: discipline
        }).subscribe(() => this.load());
      }
    });
  }

  openCreateUser() {

  }

  editUser(user: any) {

  }

  deleteUser(user: any) {

  }

  newDisciplineInvalid() {
    if (this.loading) {
      setTimeout(() => this.loading = false, 3000);
      return 'Carregando';
    }
    if (this.newSemesterNumber > 99) return 'Disciplina com mais de dois dígitos';
    if (this.disciplines.some((discipline: {
      name: string;
      course: string;
    }) => discipline.name === this.newDisciplineTitle.trim() && discipline.course === this.newCourseTitle.trim()))
      return 'Disciplina já cadastrada para esse curso esse semestre';
    return false;
  }
}
