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
  videos :Video[];
  selectedVideo:Video;
  videoPlaying:string;
  constructor(private platform: Platform, private fs:FsService){
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
      alert("Download before play!");
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
}
