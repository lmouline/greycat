import * as greycat from 'greycat';
import level from 'level';
class LevelStorage implements greycat.plugin.Storage {

    db;

    constructor(path: string) {
        this.db = level(path);
    }

    get(keys: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void {

    }

    put(stream: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
        let batch = this.db.lookupBatch();
        let it = stream.iterator();
        while (it.hasNext()) {
            let keyView = it.next();
            let valueView = it.next();
            if (valueView != null) {
                batch.put(keyView.data(), valueView.data());
            }
        }
        batch.write(function(){
            callback(true);
        })
    }

    remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
    }

    connect(graph: greycat.Graph, callback: greycat.Callback<boolean>): void {
    }

    lock(callback: greycat.Callback<greycat.struct.Buffer>): void {
    }

    unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void {
    }

    disconnect(callback: greycat.Callback<boolean>): void {
    }

}
export = LevelStorage;