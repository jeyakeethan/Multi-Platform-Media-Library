import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab1Page } from './tab1.page';
import { HttpClientModule } from '@angular/common/http';
import { SongComponent } from '../components/song/song.component';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { File } from '@ionic-native/file/ngx';
import { FileTransfer, FileTransferObject } from '@ionic-native/file-transfer/ngx';
import { FsService } from '../service/fs.service';
import { ReactiveFormsModule } from "@angular/forms";
@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule.forChild([{ path: '', component: Tab1Page }])
  ], providers: [File, FileOpener,
    FileTransfer,
    FileTransferObject,
     FsService],
  declarations: [Tab1Page, SongComponent]
})
export class Tab1PageModule {}
