import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { Candidate } from '../../core/models/candidate.model';

import { CandidateService } from '../../core/services/candidate.service';

import { AuthService } from '../../core/services/auth.service';



@Component({

  selector: 'app-my-profile',

  standalone: true,

  imports: [CommonModule, FormsModule],

  templateUrl: './my-profile.component.html',

  styleUrls: ['./my-profile.component.css']

})

export class MyProfileComponent implements OnInit {

  profile: Candidate | null = null;

  loading = true;

  saving = false;

  error = '';

  success = '';

  cvFile: File | null = null;

  editMode = false;



  constructor(

    private candidateService: CandidateService,

    private auth: AuthService

  ) {}



  ngOnInit(): void {

    this.loadProfile();

  }



  loadProfile() {

    const username = this.auth.getUsername();

    if (!username) {

      this.error = 'Session invalide';

      this.loading = false;

      return;

    }

    this.candidateService.getByUsername(username).subscribe({

      next: (p) => {

        this.profile = p;

        this.loading = false;

      },

      error: () => {

        this.error = 'Profil candidat introuvable. Contactez le recrutement.';

        this.loading = false;

      }

    });

  }



  getSkillsList(skills?: string): string[] {

    return skills ? skills.split(',').map((s) => s.trim()).filter(Boolean) : [];

  }



  onCvSelected(event: Event) {

    const input = event.target as HTMLInputElement;

    this.cvFile = input.files?.[0] ?? null;

  }



  uploadCv() {

    if (!this.profile?.id || !this.cvFile) return;

    this.candidateService.uploadCv(this.profile.id, this.cvFile).subscribe({

      next: (updated) => {

        this.profile = updated;

        this.cvFile = null;

        this.success = 'CV envoyé avec succès.';

      },

      error: () => {

        this.error = 'Échec de l\'envoi du CV';

      }

    });

  }



  downloadCv() {

    if (!this.profile?.resumeUrl) return;

    this.candidateService.downloadFile(this.profile.resumeUrl).subscribe({

      next: (blob) => {

        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');

        a.href = url;

        const filename = this.profile!.resumeUrl?.substring(this.profile!.resumeUrl.lastIndexOf('/') + 1) || 'cv.pdf';

        a.download = filename;

        a.click();

        window.URL.revokeObjectURL(url);

      },

      error: () => {

        this.error = 'Erreur lors du téléchargement du CV';

      }

    });

  }



  saveProfile() {

    if (!this.profile?.id) return;

    this.saving = true;

    this.error = '';

    this.success = '';

    this.candidateService.update(this.profile.id, this.profile).subscribe({

      next: (p) => {

        this.profile = p;

        this.saving = false;

        this.editMode = false;

        this.success = 'Profil mis à jour.';

      },

      error: () => {

        this.saving = false;

        this.error = 'Impossible de sauvegarder le profil';

      }

    });

  }

}

