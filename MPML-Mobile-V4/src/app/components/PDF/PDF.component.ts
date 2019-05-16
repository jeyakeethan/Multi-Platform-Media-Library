import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';

export class PDF{
  constructor(public name?, public author?, public date?, public size?, public id?, public available?){
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
    PDFsRetrieved :PDF[];
    sharedId:string;
    selectedPDFToShare:PDF;
    songPlaying:string;
    public searching: Boolean = false;
    public searchTerm:string = "";
    constructor(private platform: Platform, private fs:FsService){
    }
    onInit(){
      this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
      this.PDFs = this.PDFsRetrieved;
      this.platform.ready().then(()=>{
        this.PDFsRetrieved.forEach(element => {
          element.available=this.fs.checkFile(0,element.name);
        });
        this.PDFs = this.PDFsRetrieved;
      });
    }

    public cancelSearch(){
      this.searching = false;
      this.PDFs = this.PDFsRetrieved;
    }
    public setFilteredItems(){
      this.searching = true;
      if(this.searchTerm==""){
        this.PDFs = this.PDFsRetrieved;
      }
      else{
        var key = this.searchTerm.toLowerCase();
        this.PDFs = this.PDFsRetrieved.filter(song => {
          return String(song.name).toLowerCase().startsWith(key);
        });
      }
      this.searching = false;
    }

    public async deletePDF(index:number){
      this.PDFs.splice(index, 1);
      this.fs.deleteFile(0,index);
    }

    public openItem($item){
      let available = this.fs.openFile(0, $item.name);
      if(!available){
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/PDFs/"+$item.id+".pdf",'_system','location=yes');
      }
      /*if($item.available){
       this.fs.openFile(0, $item.name);
      }
      else{
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/PDFs/"+$item.id+".pdf",'_system','location=yes');
      }*/
    }

    public downloadPDF($song){
      this.platform.ready().then(()=>{
        this.fs.downloadFile(0,$song.id,$song.name);
      })
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
  /*public login() {
    if(this.username!=""&&this.password!=""){
      let success = this.fs.login(this.username, this.password);
      if(success){
        this.loginPanel=false; 
        this.fs.loadPDFsFromServer().then(PDFs=>this.PDFs=PDFs);
        alert("Login Success!");
      }
      else{
        this.username = "";
        this.password = "";
        alert("User name or Password missmatch!");
      }
    }
  }*/
  
  public reload(){
    this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
    this.PDFs = this.PDFsRetrieved;
  }
public ionViewDidEnter(): void {
  if(this.PDFs==null){
    this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
    this.PDFs = this.PDFsRetrieved;
  }
}
public share(){
  this.fs.share(0,this.selectedPDFToShare.id,this.sharedId);
}
} 
