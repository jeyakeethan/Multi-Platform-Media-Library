import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders,HttpParams} from '@angular/common/http';
import { Http } from '@angular/http';
import {Song} from '../components/song/song.component';
import {Video} from '../components/video/video.component';
import { PDF } from '../components/pdf/pdf.component';
import { File } from '@ionic-native/file/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { pathToFileURL, fileURLToPath, resolve } from 'url';
import { Storage } from '@ionic/storage';
import { FileTransfer, FileUploadOptions, FileTransferObject } from '@ionic-native/file-transfer/ngx';

export class User{
  constructor(public name?, public id?){
  }
}
@Injectable()
export class FsService {
  static user:User;
  constructor(private httpClient:HttpClient,private file: File, private filePath:FilePath, private fileOpener:FileOpener, private storage:Storage,private transfer: FileTransfer) {
    this.restoreUser();
  }
  public async restoreUser(){
    this.storage.get("user").then((user)=>{FsService.user=user;});
  }
  public getSongs(){
    let songs;
    this.loadSongsFromServer().then((loadedSongs)=>songs =loadedSongs);
    songs.forEach(element => {
        element.available=this.checkFile(0,element.name);
      });
      return songs;
  }

  public getVideos(){
    let videos;
    this.loadMoviesFromServer().then((loadedMovies)=>videos =loadedMovies);
    videos.forEach(element => {
        element.available=this.checkFile(0,element.name);
      });
      return videos;
    //let data = this.httpClient.get<any>('assets/data/videos.json')
      //  .toPromise()
        //.then(res => <Video[]> res.data)
        //.then(data => data);
       // return data;
        //this.storage.set("songs", data);
        //return this.storage.get("songs").then((data) => {return data});
  }
  public getPDFs(){
    let PDFs;
    this.loadPDFsFromServer().then((loadedPDFs)=>PDFs =loadedPDFs);
    PDFs.forEach(element => {
        element.available=this.checkFile(0,element.name);
      });
      return PDFs;
        //this.storage.set("songs", data);
        //return this.storage.get("songs").then((data) => {return data});
  }

  //song name is passed as id here
  public openFile(mode?, id?):Boolean{
    let available:boolean=false;
    switch(mode){
      case 0:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then(
          (res) => res,
          (err) => false
      ).then((fileAvailable)=>{
          let path = this.file.externalRootDirectory+'MediaLibrary/songs/'+id+'.mp3';
          this.fileOpener.open(path, 'audio/mpeg3').then((success)=>{available=true;});
        });
      break;
      case 1:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Movies/'+id+'.mp4').then(
          (res) => res,
          (err) => false
      ).then((fileAvailable)=>{
          let path = this.file.externalRootDirectory+'MediaLibrary/Movies/'+id+'.mp4';
          this.fileOpener.open(path, 'video/mpeg').then((success)=>{available=true;});
        });
      break;
      case 2:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/PDFs/'+id+'.pdf').then(
          (res) => res,
          (err) => false
      ).then((fileAvailable)=>{
          let path = this.file.externalRootDirectory+'MediaLibrary/PDFs/'+id+'.pdf';
          this.fileOpener.open(path, 'application/pdf').then((success)=>{available=true;});
        });
      break;
    }
    return available;
  }
  
  //song name is passed as id here
  public checkFile(mode?, id?):boolean{
    let available=false;
    switch(mode){
      case 0:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then(
          (err) => false,
          (res) => res
      ).then((res)=>available=true);
      break;
      case 1:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Movies/'+id+'.mp4').then(
          (res) => res,
          (err) => false
      ).then((fileAvailable)=>available=true);
      break;
      case 2:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/PDFs/'+id+'.pdf').then(
          (res) => res,
          (err) => false
      ).then((fileAvailable)=>available=true);
      break;
    }
    return available;
  }
  loadSongsFromServer(){
    if(FsService.user!=null){
      let songs =  this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?mode=songs&id='+FsService.user.id)
      .toPromise()
      .then(res => <Song[]> res.data)
      .then(data =>  data);
      return songs;
    }
  }

  loadMoviesFromServer(){
    if(FsService.user!=null){
      let videos = this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?mode=movies&id='+FsService.user.id)
      .toPromise()
      .then(res => <Video[]> res.data)
      .then(data => data);
      this.storage.set("videos", videos);
      return videos;
    }
  }

  loadPDFsFromServer(){
    if(FsService.user!=null){
      let PDFs = this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?mode=PDFs&id='+FsService.user.id)
      .toPromise()
      .then(res => <PDF[]> res.data)
      .then(data => data);
      this.storage.set("PDFs", PDFs);
      return PDFs;
    } 
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

  
  downloadFile(mode?, id?, name?){
    const fileTransfer: FileTransferObject = this.transfer.create();
    switch(mode){
      case 0:
      //this.fileOpener.open(id, 'audio/mpeg3');
        this.file.createDir(this.file.externalRootDirectory, "MediaLibrary",false);
        this.file.createDir(this.file.externalRootDirectory+"MediaLibrary", "Songs",false);
        let path =  this.file.externalRootDirectory+'MediaLibrary/Songs/'+name+".mp3";
        const url = "https://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+id+".mp3";
        fileTransfer.download(url, path).then((entry) => {
          console.log('download complete: ' + entry.toURL());
          alert("Song has been downloaded!");
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
      case 1:
        this.file.createDir(this.file.externalRootDirectory, "MediaLibrary",false);
        this.file.createDir(this.file.externalRootDirectory+"MediaLibrary", "Movies",false);
        let path1 =  this.file.externalRootDirectory+'MediaLibrary/Movies/'+id+".mp4";
        const url1 = "https://medialibraryweb.000webhostapp.com/MediaLibrary/Movies/"+id+".mp4";
        fileTransfer.download(url1, path1).then((entry) => {
          console.log('download complete: ' + entry.toURL());
        });
      break;
      case 2:
        this.file.createDir(this.file.externalRootDirectory, "MediaLibrary",false);
        this.file.createDir(this.file.externalRootDirectory+"MediaLibrary", "PDFs",false);
        let path2 =  this.file.externalRootDirectory+'MediaLibrary/PDFs/'+id+".pdf";
        const url2 = "https://medialibraryweb.000webhostapp.com/MediaLibrary/PDFs/"+id+".pdf";
        fileTransfer.download(url2, path2).then((entry) => {
          console.log('download complete: ' + entry.toURL());
        });
      break;
    }
  }
  deleteFileServer(mode:number,id:number){
    //this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/manage_data.php?mode='+mode+'&id='+id).subscribe();
    let url ="https://medialibraryweb.000webhostapp.com/manage_data.php";
    let  params = {id:id.toString(),mode:mode.toString(),key:'delete'};
    console.log(params);
    this.httpClient.post(url,JSON.stringify(params)).subscribe((response) => {
      console.log(response);
    }, error => {
      console.log(error);
    });
  } 

  deleteFile(mode:number,id?):any{
    this.deleteFileServer(mode,id);
    switch(mode){
      case 0:
        this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Songs/'+id+'.mp3').then((fileAvailable)=>{
          if(fileAvailable){
            this.file.removeFile(this.file.externalRootDirectory+'MediaLibrary/Songs/',id+'.mp3');
            return true;
          }
          else{
            return false;
          }
        });
      break;
      case 1:
      this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/Movies/'+id+'.mp4').then((fileAvailable)=>{
        if(fileAvailable){
          this.file.removeFile(this.file.externalRootDirectory+'MediaLibrary/Movies/',id+'.mp4');
          return true;
        }
        else{
          return false;
        }
      });
      break;
      case 2:
      this.file.checkFile(this.file.externalRootDirectory, 'MediaLibrary/PDFs/'+id+'.pdf').then((fileAvailable)=>{
        if(fileAvailable){
          this.file.removeFile(this.file.externalRootDirectory+'MediaLibrary/PDFs/',id+'.pdf');
          return true;
        }
        else{
          return false;
        }
      });
      break;
    }
  }
  public login(name:string, pass:string):boolean{
    /*this.httpClient.get<any>('https://medialibraryweb.000webhostapp.com/retrive_data.php?username='+name+'&password='+pass)
    .toPromise()
    .then(res => <User[]> res.data)*/
    const params = {username:name,password:pass};
    this.httpClient.post("https://medialibraryweb.000webhostapp.com/login.php",JSON.stringify(params)).toPromise().then(res =>  <User>res.valueOf()['data'][0]).then(data => FsService.user=data);
    this.storage.set("user",FsService.user)
    return (FsService.user.name==name);
  }
  public logout():boolean{
    this.storage.remove("user");
    return true;
  }
  public share(mode:number,id:number,user:string){
    let url ="https://medialibraryweb.000webhostapp.com/manage_data.php";
    let  params = {user:user, id:id.toString(),mode:mode.toString(),key:'create'};
    console.log(params);
    this.httpClient.post(url,JSON.stringify(params)).subscribe((response) => {
      console.log(response);
    }, error => {
      console.log(error);
    });
  }
}
 