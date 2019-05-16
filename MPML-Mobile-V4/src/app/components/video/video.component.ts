import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';

export class Video{
  constructor(public name?, public date?, public duration?, public size?, public id?, public cover?, public available?){
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
  videos :Video[];
  videosRetrieved :Video[];
  sharedId:string;
  selectedVideoToShare:Video;
  videoPlaying:string;
  public searching: Boolean = false;
  public searchTerm:string = "";
  constructor(private platform: Platform, private fs:FsService){
  }
  onInit(){
    this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
    this.videos = this.videosRetrieved;
    this.platform.ready().then(()=>{
      this.videosRetrieved.forEach(element => {
        element.available=this.fs.checkFile(0,element.name);
      });
      this.videos = this.videosRetrieved;
    });
  }

  public cancelSearch(){
    this.searching = false;
    this.videos = this.videosRetrieved;
  }
  public setFilteredItems(){
    this.searching = true;
    if(this.searchTerm==""){
      this.videos = this.videosRetrieved;
    }
    else{
      var key = this.searchTerm.toLowerCase();
      this.videos = this.videosRetrieved.filter(video => {
        return String(video.name).toLowerCase().startsWith(key);
      });
    }
    this.searching = false;
  }

  public async deleteVideo(index:number){
    this.videos.splice(index, 1);
    this.fs.deleteFile(0,index);
  }

  public openItem($item){
    let available = this.fs.openFile(0, $item.name);
    if(!available){
      window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Videos/"+$item.id+".mp4",'_system','location=yes');
    }
    /*if($item.available){
     this.fs.openFile(0, $item.name);
    }
    else{
      window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Videos/"+$item.id+".mp4",'_system','location=yes');
    }*/
  }

  public downloadVideo($video){
    this.platform.ready().then(()=>{
      this.fs.downloadFile(0,$video.id,$video.name);
    })
  }
  sortItems(tag){
    switch(tag){
      case 'name':
        this.videos = this.videos.sort(function(a, b){
          if(a.name < b.name)return -1;else return 1;
        });
      break;
      case 'date':
        this.videos = this.videos.sort(function(a, b){
          if(a.date < b.date)return -1;else return 1;
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
  public reload(){
    this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
    this.videos = this.videosRetrieved;
  }
public ionViewDidEnter(): void {
  if(this.videos==null){
    this.fs.loadMoviesFromServer().then(videos=>this.videosRetrieved=videos);
    this.videos = this.videosRetrieved;
  }
}
public share(){
  this.fs.share(0,this.selectedVideoToShare.id,this.sharedId);
}
}
