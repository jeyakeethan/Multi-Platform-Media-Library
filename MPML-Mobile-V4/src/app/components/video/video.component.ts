import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';

export class Video{
  constructor(public name?, public album?, public artist?, public duration?, public size?, public id?){
  }
}

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss'],
  providers:[FsService,]
})
export class VideoComponent{
  username:string;
  password:string;
  loginPanel:boolean;
  videos :Video[];
  sharedId:string;
  selectedVideoToShare:Video;
  videoPlaying:string;
  constructor(private platform: Platform, private fs:FsService){
    this.loginPanel=FsService.user==null?true:false;
      //this.fs.getVideos().then(videos=>this.videos=videos);
    platform.ready()
    .then(() => {
      this.fs.getVideos().then(videos=>this.videos=videos);
    })
  }
  public async deleteVideo(index:number){
    this.videos.splice(index, 1);
    this.fs.deleteFile(1,this.videos[index].id);
  }

  public openItem($item){
    let available = this.fs.openFile(1, $item.id);
    if(!available){
      window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Videos/"+$item.id+".mp4",'_system','location=yes');
    }
  }

  public downloadVideo($id){
    this.fs.downloadFile(1,$id);
  }

  sortItems(tag){
    switch(tag){
      case 'name':
        this.videos = this.videos.sort(function(a, b){
          if(a.name < b.name)return -1;else return 1;
        });
      break;
      case 'album':
        this.videos = this.videos.sort(function(a, b){
          if(a.album < b.album)return -1;else return 1;
        });
      break;
      case 'artist':
        this.videos = this.videos.sort(function(a, b){
          if(a.artist < b.artist)return -1;else return 1;
        });
      break;
      case 'duration':
        this.videos = this.videos.sort(function(a, b){
            return a.duration - b.duration;
        });
      break;
      case 'size':
        this.videos = this.videos.sort(function(a, b){
            return a.size - b.size;
        });
      break;
    }
  }
  reload(){
    this.loginPanel=FsService.user==null?true:false;
    this.fs.loadMoviesFromServer().then(videos=>this.videos=videos);
  }
  public login() {
    if(this.username!=""&&this.password!=""){
      let success = this.fs.login(this.username, this.password);
      if(success){
        this.loginPanel=false;
        this.fs.loadSongsFromServer().then(videos=>this.videos=videos);
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
    if(this.videos==null){
      this.fs.loadSongsFromServer().then(videos=>this.videos=videos);
    }
      this.loginPanel=FsService.user==null?true:false;
  }
  public share(){
    this.fs.share(1,this.selectedVideoToShare.id,this.sharedId);
  }
}
