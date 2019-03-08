function list(dir) {
   const walk = entry => {
     return new Promise((resolve, reject) => {
       fs.exists(entry, exists => {
         if (!exists) {
           return resolve({});
         }
         return resolve(new Promise((resolve, reject) => {
           fs.lstat(entry, (err, stats) => {
             if (err) {
               return reject(err);
             }
             if (!stats.isDirectory()) {
               return resolve({
                 // path: entry,
                 // type: 'file',
                 name: path.basename(entry),
                 time: stats.mtime,
                 size: stats.size
               });
             }
             resolve(new Promise((resolve, reject) => {
               fs.readdir(entry, (err, files) => {
                 if (err) {
                   return reject(err);
                 }
                 Promise.all(files.map(child => walk(path.join(entry, child)))).then(children => {
                   resolve({
                     // path: entry,
                     // type: 'folder',
                     name: path.basename(entry),
                     time: stats.mtime,
                     entries: children
                   });
                 }).catch(err => {
                   reject(err);
                 });
               });
             }));
           });
         }));
       });
     });
   }

   return walk(dir);
 }
 list("d:");