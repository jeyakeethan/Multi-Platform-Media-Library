import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Song} from '../components/song/song.component';
import { Video } from '../components/video/video.component';
import { PDF } from '../components/pdf/pdf.component';
import { File } from '@ionic-native/file/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { pathToFileURL, fileURLToPath, resolve } from 'url';
import { Storage } from '@ionic/storage';
import { FileTransfer, FileUploadOptions, FileTransferObject } from '@ionic-native/file-transfer/ngx';


@Injectable()
export class FsService {
  constructor(private httpClient:HttpClient,private file: File, private filePath:FilePath, private fileOpener:FileOpener, private storage:Storage,private transfer: FileTransfer) {
    
  }
  
  getSongs(){
    let data = this.httpClient.get<any>('assets/data/songs.json')
        .toPromise()
        .then(res => <Song[]> res.data)
        .then(data => data);
        return data;
        //this.storage.set("songs", data);
        //return this.storage.get("songs").then((data) => {return data});
  }

  getVideos(){
    let data = this.httpClient.get<any>('assets/data/videos.json')
        .toPromise()
        .then(res => <Video[]> res.data)
        .then(data => data);
        return data;
        //this.storage.set("songs", data);
        //return this.storage.get("songs").then((data) => {return data});
  }
  getPDFs(){
    let data = this.httpClient.get<any>('assets/data/PDFs.json')
        .toPromise()
        .then(res => <PDF[]> res.data)
        .then(data => data);
        return data;
        //this.storage.set("songs", data);
        //return this.storage.get("songs").then((data) => {return data});
  }
  openFile(mode?, id?):Boolean{
    let available:boolean=false;
    switch(mode){
      case 0:    
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then((fileAvailable)=>{
          available=true;
          let path = this.file.externalRootDirectory+'MediaLibrary/songs/'+id+'.mp3';
          this.fileOpener.open(path, 'audio/mpeg3');
        });
      break;
      case 1:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Movies/'+id+'.mp4').then((fileAvailable)=>{
          available=true;
          let path = this.file.externalRootDirectory+'MediaLibrary/Movies/'+id+'.mp4';
          this.fileOpener.open(path, 'video/mpeg');
        });
      break;
      case 2:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/PDFs/'+id+'.pdf').then((fileAvailable)=>{
          available=true;
          let path = this.file.externalRootDirectory+'MediaLibrary/PDFs/'+id+'.pdf';
          this.fileOpener.open(path, 'application/pdf');
        });
      break;
    }
    return available;
  }

  loadSongsFromServer(){
      let songs = this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?mode=songs')
      .toPromise()
      .then(res => <Song[]> res.data)
      .then(data => data);
      this.storage.set("songs", songs);
      return songs;
  }

 
   getFile(path:string){
    /*return this.file.resolveDirectoryUrl(path).then(dir => {
      return this.file.getFile(dir, path, {create:false})
    });*/
    let song:any;
    this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?song=1')
      .toPromise()
      .then(res => song=res);
    var blob = new Blob([song.buffer], {type: 'audio/mpeg3'}); // pass a useful mime type here
    var url = URL.createObjectURL(blob);
    return url
  }

  listDir(dir:string, path:string){
    return this.file.listDir(dir, path);
  }

  downloadFile(mode?, id? ){
    const fileTransfer: FileTransferObject = this.transfer.create();
    switch(mode){
      case 0:
      //this.fileOpener.open(id, 'audio/mpeg3');
      let path =  this.file.externalRootDirectory+'MediaLibrary/songs'+id+".mp3";
            const url = "https://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+id+".mp3";
            fileTransfer.download(url, path).then((entry) => {
              console.log('download complete: ' + entry.toURL());
            });
        /*this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then((fileAvailable)=>{
          if(!fileAvailable){
            let path =  this.file.tempDirectory+'MediaLibrary/songs';
            const url = "https://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+id+".mp3";
            fileTransfer.download(url, path).then((entry) => {
              console.log('download complete: ' + entry.toURL());
            });
          }
        });*/
      break;
    }
  }
  deleteFileServer(mode?,id?){
    //deletefrom server code
  }

  deleteFile(mode?,id?):any{
    this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then((fileAvailable)=>{
      if(fileAvailable){
        let path =  this.file.externalRootDirectory+'MediaLibrary/songs/'+id+'.mp3';
        this.file.removeFile(this.file.externalRootDirectory+'MediaLibrary/Songs/',id+'.mp3');
        this.deleteFileServer(mode,id);
        return true;
      }
      else{
        return false;
      }
    });
  }
}
