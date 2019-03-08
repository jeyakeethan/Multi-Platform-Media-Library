import { Component, OnInit} from '@angular/core';
import { File } from '@ionic-native/file/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';
import { Platform, LoadingController } from '@ionic/angular';

import {FsService} from "../service/fs.service";

@Component({
  selector: 'app-tab4',
  templateUrl: 'tab4.page.html',
  styleUrls: ['tab4.page.scss'],
  providers:[File, FsService]
})
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
  };*/
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