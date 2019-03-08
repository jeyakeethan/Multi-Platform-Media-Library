import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab2Page } from './tab2.page';
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
import { VideoComponent } from '../components/video/video.component';


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
    RouterModule.forChild([{ path: '', component: Tab2Page }])
  ], providers: [File, FilePath, FileOpener,
    //MusicControls,
     FsService],
  declarations: [Tab2Page, VideoComponent]
})
export class Tab2PageModule {}
