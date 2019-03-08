import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';

export class PDF{
  constructor(public name?, public author?, public date?, public size?, public id?){
  }
}

@Component({
  selector: 'app-PDF',
  templateUrl: './PDF.component.html',
  styleUrls: ['./PDF.component.scss'],
  providers:[FsService,]
})
export class PDFComponent{
  PDFs :PDF[];
  selectedPDF:PDF;
  PDFPlaying:string;
  constructor(private platform: Platform, private fs:FsService){
    platform.ready()
    .then(() => {
      this.fs.getPDFs().then(PDFs=>this.PDFs=PDFs);
    })
  }
  public async deletePDF(index:number){
    this.PDFs.splice(index, 1);
    this.fs.deleteFile(2,this.PDFs[index].id);
  }

  public openItem($item){
    let available = this.fs.openFile(2, $item.id);
    if(!available){
      alert("Download before play!");
    }
  }

  public downloadPDF($id){
    this.fs.downloadFile(2,$id);
  }

  sortItems(tag){
    switch(tag){
      case 'name':
        this.PDFs = this.PDFs.sort(function(a, b){
          if(a.name < b.name)return -1;else return 1;
        });
      break;
      case 'author':
        this.PDFs = this.PDFs.sort(function(a, b){
          if(a.author < b.author)return -1;else return 1;
        });
      break;
      case 'date':
        this.PDFs = this.PDFs.sort(function(a, b){
          if(a.date < b.date)return -1;else return 1;
        });
      break;
      case 'size':
        this.PDFs = this.PDFs.sort(function(a, b){
            return a.size - b.size;
        });
      break;
    }
  }
}
