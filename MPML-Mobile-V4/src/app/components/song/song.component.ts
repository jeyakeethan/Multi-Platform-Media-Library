import { Component, OnInit, ViewChild } from '@angular/core';
import { Platform } from '@ionic/angular';
//import { MusicControls } from '@ionic-native/music-controls/ngx';
import { FsService } from 'src/app/service/fs.service';
export class Song{
  constructor(public name?, public album?, public artist?, public duration?, public size?,public id?){
  }
}
  

@Component({
  selector: 'app-song',
  templateUrl: './song.component.html',
  styleUrls: ['./song.component.scss'],
  providers:[FsService,]
})
export class SongComponent{

    songs :Song[];
    selectedSong:Song;
    display:boolean;
    songPlaying:string;
    constructor(private platform: Platform, private fs:FsService){
      
      this.display=false;
     platform.ready()
      .then(() => {
        this.fs.loadSongsFromServer().then(songs=>this.songs=songs);
      })
    }
    public deleteSong(index:number){
      this.songs.splice(index, 1);
      this.fs.deleteFile(0,this.songs[index].id);
    }

    public openItem($item){
      let available = this.fs.openFile(0, $item.id);
      if(!available){
        this.display=true;
    // this.songPlaying=
        window.open("http://medialibraryweb.000webhostapp.com/MediaLibrary/Songs/"+$item.id+".mp3",'_system','location=yes');
      }
    }

    public downloadSong($id){
      this.fs.downloadFile(0,$id);
    }

    sortItems(tag){
      switch(tag){
        case 'name':
          this.songs = this.songs.sort(function(a, b){
            if(a.name < b.name)return -1;else return 1;
          });
        break;
        case 'album':
          this.songs = this.songs.sort(function(a, b){
            if(a.album < b.album)return -1;else return 1;
          });
        break;
        case 'artist':
          this.songs = this.songs.sort(function(a, b){
            if(a.artist < b.artist)return -1;else return 1;
          });
        break;
        case 'duration':
          this.songs = this.songs.sort(function(a, b){
              return a.duration - b.duration;
          });
        break;
        case 'size':
          this.songs = this.songs.sort(function(a, b){
              return a.size - b.size;
          });
        break;
      }
    }
}
