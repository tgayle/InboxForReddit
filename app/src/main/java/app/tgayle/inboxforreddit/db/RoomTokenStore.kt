package app.tgayle.inboxforreddit.db

import net.dean.jraw.models.PersistedAuthData
import net.dean.jraw.oauth.DeferredPersistentTokenStore

class RoomTokenStore(val appDatabase: AppDatabase): DeferredPersistentTokenStore() {
    init {
        autoPersist = true
        load()
    }

    override fun doLoad(): Map<String, PersistedAuthData> {
        val accounts = appDatabase.dao().getAllSync()
        val loadedData = HashMap<String, PersistedAuthData>()
        accounts.forEach {
            loadedData[it.name] = PersistedAuthData.create(null, it.refreshToken)
        }
        return loadedData
    }

    override fun doPersist(data: Map<String, PersistedAuthData>) {
        for ((username, authData) in data) {
            appDatabase.dao().updateUserAuthData(username, authData.refreshToken)
        }
    }


}