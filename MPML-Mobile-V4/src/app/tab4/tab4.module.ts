import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
//import { InputTextModule } from 'primeng/inputtext';
//import { ButtonModule } from 'primeng/button';
//import { TableModule } from 'primeng/table';
//import { DialogModule } from 'primeng/dialog';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { File } from '@ionic-native/file/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';

//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from '../service/fs.service';
import { Tab4Page } from './tab4.page';
@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forChild([{ path: '', component: Tab4Page }])
  ],
  declarations: [Tab4Page],
  providers:[File,FilePath,FileOpener,FsService]
})
export class Tab4PageModule {}
