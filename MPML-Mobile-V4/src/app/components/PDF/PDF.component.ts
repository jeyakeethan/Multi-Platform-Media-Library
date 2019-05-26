import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
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
    pdfPlaying:string;
    public searching: Boolean = false;
    public searchTerm:string = "";
    constructor(private platform: Platform, private fs:FsService){
    }
    onInit(){
      this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
      this.PDFs = this.PDFsRetrieved;
      /*this.platform.ready().then(()=>{
        this.PDFsRetrieved.forEach(element => {
          element.available=this.fs.checkFile(2,element.name);
        });
      });*/
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
        this.PDFs = this.PDFsRetrieved.filter(pdf => {
          return String(pdf.name).toLowerCase().startsWith(key);
        });
      }
      this.searching = false;
    }

    public async deletePDF(index:number){
      //var result = confirm("Want to delete?");
      //if (result) {
        this.PDFs.splice(index, 1);
        this.fs.deleteFile(2,index);
      //}
    }

    public openItem($item){
      let available = this.fs.openFile(2, $item.name);
      if(!available){
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/PDFs/"+$item.id+".pdf",'_system','location=yes');
      }
    }

    public downloadPDF($pdf){
      this.platform.ready().then(()=>{
        this.fs.downloadFile(2,$pdf.id,$pdf.name);
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
  
  public reload(){
    if(FsService.user!=null){
      this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
      this.PDFs = this.PDFsRetrieved;
    }else{
      alert("Go to Setting tab and login First!");
    }
  }
public ionViewDidEnter(): void {
  if(this.PDFs==null){
    this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
    this.PDFs = this.PDFsRetrieved;
  }
}
public share(){
  this.fs.share(2,this.selectedPDFToShare.id,this.sharedId);
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