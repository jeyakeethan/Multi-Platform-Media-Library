import { Component, OnInit} from '@angular/core';
import {FsService, User} from "../service/fs.service";
import { NavController } from '@ionic/angular';
import {SongComponent} from '../components/song/song.component';
import {VideoComponent} from '../components/video/video.component';
import {PDFComponent} from '../components/pdf/PDF.component';
@Component({
  selector: 'app-tab4',
  templateUrl: 'tab4.page.html',
  styleUrls: ['tab4.page.scss'],
  providers:[FsService,]
})
export class Tab4Page {
    username:string;
    password:string;
    loggedIn:boolean=false;
    user:User;
    constructor(private fileService:FsService, private nav:NavController) {
      this.user=FsService.user;
      if(this.user!=null){
        this.loggedIn=true;
        this.username=FsService.user.name;
      }
    } 

    public login() {
      if(this.username!=""&&this.password!=""){
        let success = this.fileService.login(this.username, this.password);
        if(success){
          alert("Login Success!");
          this.loggedIn=true;
        }
        else{
          this.username = "";
          this.password = "";
          alert("User name or Password missmatch!");
        }
      }
    }
    public logout(){
      let success = this.fileService.logout();
        if(success){
          alert("Logout success!");
          FsService.user=null;
          this.loggedIn=false;
          this.nav.pop();
          this.nav.pop();
          this.nav.pop();
        }
    }
}
/*
export class Tab4Page {
  selectPath;
  savedParentNativeURLs = [];
  fileList;
  songDirectories = [];
  movieDirectories = [];
  imageDirectories = [];
  mediaConfigMode:string;
  currentPath:string;
  ROOT_DIRECTORY:string;
  constructor(private file:File, private platform:Platform, private fs:FsService, private loadingCtrl: LoadingController ){
    this.selectPath = false;
      this.ROOT_DIRECTORY =file.externalRootDirectory;


      if(!this.currentPath) this.currentPath = ".";

      fs.listDir(this.ROOT_DIRECTORY, this.currentPath).then((files) => {
        this.fileList=files;
      }).catch((err)=>{
        console.log(err);
      });
  }

  /*listDir = (path, dirName) => {
    this.file
      .listDir(path, dirName)
      .then(entries => {
        this.items = entries;
      })
      .catch(this.handleError);
  };
  goDown = item => {
    const parentNativeURL = item.nativeURL.replace(item.name, "");
    this.savedParentNativeURLs.push(parentNativeURL);
  
    this.fs.listDir(parentNativeURL,item.name);
  };
  goUp = () => {
    const parentNativeURL = this.savedParentNativeURLs.pop();
  
    this.fs.listDir(parentNativeURL,"");
  };

  handleError = error => {
    console.log("error reading,", error);
  };

  selectSongDir(){
    this.selectPath=true;
    this.mediaConfigMode='song';
  }
  selectMovieDir(){
    this.selectPath=true;
    this.mediaConfigMode='movie';
  }
  selectImageDir(){
    this.selectPath=true;
    this.mediaConfigMode='image';
  }
  addDirectory(item){
    switch(this.mediaConfigMode){
      case 'song':
        if(this.songDirectories.includes(item)){
          this.songDirectories.splice( this.songDirectories.indexOf(item), 1 );
        }
        else{
          this.songDirectories.push(item);
        }
      break;
      case 'movie':
        true;
      break;
      case 'image':
        true;
      break;
    }
    
  }
  saveConfig(){
    switch(this.mediaConfigMode){
      case 'song':
        this.file.writeFile(this.file.dataDirectory,'songdirectories.json', JSON.stringify(this.songDirectories), {replace:true});
      break;
      case 'movie':
        this.file.writeFile(this.file.dataDirectory,'moviedirectories.json', JSON.stringify(this.movieDirectories), {replace:true});
      break;
      case 'image':
        this.file.writeFile(this.file.dataDirectory,'imagedirectories.json', JSON.stringify(this.imageDirectories), {replace:true});
      break;
    }
  }
}
*/