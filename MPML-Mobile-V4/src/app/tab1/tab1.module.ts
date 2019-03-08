import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab1Page } from './tab1.page';
import { HttpClientModule } from '@angular/common/http';
//import { InputTextModule } from 'primeng/inputtext';
//import { ButtonModule } from 'primeng/button';
//import { TableModule } from 'primeng/table';
//import { DialogModule } from 'primeng/dialog';
import { SongComponent } from '../components/song/song.component';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { File } from '@ionic-native/file/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';

//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from '../service/fs.service';

@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
//    InputTextModule,
//    ButtonModule,
//    TableModule,
//    DialogModule,
    RouterModule.forChild([{ path: '', component: Tab1Page }])
  ], providers: [File, FilePath, FileOpener,
    //MusicControls,
     FsService],
  declarations: [Tab1Page, SongComponent]
})
export class Tab1PageModule {}
