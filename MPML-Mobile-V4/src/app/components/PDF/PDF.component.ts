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
  
  username:string;
  password:string;
  PDFs :PDF[];
  sharedId:string;
  selectedPDFToShare:PDF;
  PDFPlaying:string;
  loginPanel:boolean;
  constructor(private platform: Platform, private fs:FsService){
    this.loginPanel=FsService.user==null?true:false;
    
    platform.ready()
    .then(() => {
      this.fs.getPDFs().then(PDFs=>this.PDFs=PDFs);
      //this.fs.getPDFs().then(PDFs=>this.PDFs=PDFs);
    })
  }
  public async deletePDF(index:number){
    this.PDFs.splice(index, 1);
    this.fs.deleteFile(2,this.PDFs[index].id);
  }

  public openItem($item){
    let available = this.fs.openFile(2, $item.id);
    if(!available){
      window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/PDFs/"+$item.id+".pdf",'_system','location=yes');
    }
  }

  public downloadPDF($id){
    this.fs.downloadFile(2,$id);
  }
  reload(){
    this.loginPanel=FsService.user==null?true:false;
    this.fs.loadPDFsFromServer().then(PDFs=>this.PDFs=PDFs);
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
  public login() {
    if(this.username!=""&&this.password!=""){
      let success = this.fs.login(this.username, this.password);
      if(success){
        this.loginPanel=false; 
        this.fs.loadSongsFromServer().then(PDFs=>this.PDFs=PDFs);
        alert("Login Success!");
      }
      else{
        this.username = "";
        this.password = "";
        alert("User name or Password missmatch!");
      }
    }
  }
  public ionViewWillEnter(): void {
    if(this.PDFs==null){
      this.fs.loadSongsFromServer().then(PDFs=>this.PDFs=PDFs);
    }
      this.loginPanel=FsService.user==null?true:false;
  }
  public share(){
    this.fs.share(2,this.selectedPDFToShare.id,this.sharedId);
  }
} 
