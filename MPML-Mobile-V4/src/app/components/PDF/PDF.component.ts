import { Component, OnInit } from '@angular/core';
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

export class PDFComponent implements OnInit{
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
    async ngOnInit(){
      await this.delay(300);
      this.platform.ready().then(()=>{
        this.reload();
      /*this.platform.ready().then(()=>{
        this.PDFsRetrieved.forEach(element => {
          element.available=this.fs.checkFile(2,element.name);
        });
      });*/
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
      let available = false;
      if(this.platform.is("android")){
        available = <boolean>this.fs.openFile(2, $item.name);
      }
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
  
  public async reload(){
    if(FsService.user!=null){
      await this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
      this.PDFs = this.PDFsRetrieved;
    }else{
      alert("Go to Setting tab and login First!");
    }
  }
public async ionViewDidEnter() {
  if(this.PDFs==null){
    await this.fs.loadPDFsFromServer().then(PDFs=>this.PDFsRetrieved=PDFs);
    this.PDFs = this.PDFsRetrieved;
  }
}
public share(){
  this.fs.share(2,this.selectedPDFToShare.id,this.sharedId);
}
delay(ms: number) {
  return new Promise( resolve => setTimeout(resolve, ms) );
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