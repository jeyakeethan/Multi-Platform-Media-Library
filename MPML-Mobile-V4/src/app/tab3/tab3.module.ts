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

import { FileTransfer, FileTransferObject } from '@ionic-native/file-transfer/ngx';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from '../service/fs.service';
import { PDFComponent } from '../components/PDF/PDF.component';

import { Tab3Page } from './tab3.page';

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
    RouterModule.forChild([{ path: '', component: Tab3Page }])
  ], providers: [File, FilePath, FileOpener,
    FileTransfer,
    FileTransferObject,
    //MusicControls,
     FsService],
  declarations: [Tab3Page, PDFComponent]
})
export class Tab3PageModule {}